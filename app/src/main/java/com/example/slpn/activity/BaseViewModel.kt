package com.example.inzynierka.aktywnosci

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.slpn.fragmenty.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    val repository = FirebaseRepository()

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }
    var isConnectedToTheInternet: MutableLiveData<Boolean> = MutableLiveData(false)

    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    fun checkInternetConnection(application: Application) {
        viewModelScope.launch(exceptionHandler) {
            val connectivityManager =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            isConnectedToTheInternet.value =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null

            callbackFlow {
                val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onUnavailable() {
                        isConnectedToTheInternet.postValue(false)
                        trySend(false).isSuccess
                    }

                    override fun onAvailable(network: Network) {
                        isConnectedToTheInternet.postValue(true)
                        trySend(true).isSuccess
                    }

                    override fun onLost(network: Network) {
                        isConnectedToTheInternet.postValue(false)
                        trySend(false).isSuccess
                    }
                }

                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(request, networkStatusCallback)

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(networkStatusCallback)
                }
            }.map(onUnavailable = { MyState.Error }, onAvailable = { MyState.Fetched }).asLiveData(Dispatchers.IO)
        }
    }

    inline fun <Result> Flow<Boolean>.map(
        crossinline onUnavailable: suspend () -> Result,
        crossinline onAvailable: suspend () -> Result,
    ): Flow<Result> = map { status ->
        isConnectedToTheInternet.postValue(status)
        when (status) {
            false -> onUnavailable()
            true -> onAvailable()
        }
    }
}
sealed class MyState {
    object Fetched : MyState()
    object Error : MyState()
}

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}