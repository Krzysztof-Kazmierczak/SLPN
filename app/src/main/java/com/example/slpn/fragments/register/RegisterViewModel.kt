package com.example.slpn.fragments.register

import com.example.inzynierka.aktywnosci.BaseViewModel
import com.example.slpn.data.User

class RegisterViewModel : BaseViewModel() {
    //Tworzenie w bazie dancyh nowego dokumentu dla użytkownika
    fun createNewUser(user: User){
        repository.createNewUser(user)
    }
}