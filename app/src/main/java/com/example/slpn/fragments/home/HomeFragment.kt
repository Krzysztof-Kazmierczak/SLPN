package com.example.slpn.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.slpn.constants.Constants
import androidx.lifecycle.ViewModelProvider
import com.example.inzynierka.firebase.NotificationData
import com.example.inzynierka.firebase.PushNotification
import com.example.slpn.R
import com.example.slpn.firebase.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    //Publikacja notyfikacji że wyciągamy paczkę
    private fun notyfiactionFunctionSend(numberPack:String,numberBox:String,tokenUser:String){
        val tresc1 = "Twoja paczka o numerze id "
        val tresc2 = " zostala wyjeta ze skrytki: "
        val tresc3 = " Minal Twoj czas na odbioru paczki."

        val mess = tresc1 + numberPack + tresc2 + numberBox + tresc3
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
        val notification = PushNotification(
            data = NotificationData("Wyciagnieto paczke", mess, 10, false),
            to = tokenUser)
        sendNotification(notification)
    }
    //Funkacjia wysyłająca notyfikacje
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.i("MyTag", "Response ${Gson().toJson(response)}")
            } else{
                Log.i("MyTag", "Response ELSE ${response.message()}")
            }
        } catch (e: Exception){
            Log.i("MyTag", "Response Exception ${e.message}")
        }
    }
}