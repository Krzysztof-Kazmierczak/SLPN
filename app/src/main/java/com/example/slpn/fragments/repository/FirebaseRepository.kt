package com.example.slpn.fragmenty.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.slpn.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val REPO_DEBUG = "REPO_DEBUG"

    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()


    //Funkcja zwracająca informację o użytkowniku który aktualnie jest zalogowany
    fun getUserData(): LiveData<User> {
        val cloudResult = MutableLiveData<User>()
        val uid = auth.currentUser?.uid
        cloud.collection("user")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                listOf(user?.paczki.toString())
                cloudResult.postValue(user)
            }
            .addOnFailureListener {
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }
    //Funkcja aktualizujaca token użytkownika
    fun pushToken(token: String) {
        val uid = auth.currentUser?.uid
        cloud.collection("user")
            .document(uid!!)
            .update("token",token)
            .addOnSuccessListener {
                Log.d("Zaktualzowano token ", token)
            }
            .addOnFailureListener {
                Log.d(REPO_DEBUG,it.message.toString())
            }
    }
    //Funkcja tworząca nowego użytkownika
    fun createNewUser(user: User) {
        cloud.collection("user")
            .document(user.uid!!)
            .set(user)
    }
}