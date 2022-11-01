package com.example.slpn.data
//Klasa przetrzymująca wygląd kolekcji "user" w FB
data class User(
    val uid:String? = null,
    val name: String? = null,
    val surname: String? = null,
    val email: String? = null,
    val toMePackID: String? = null,
    val phone: String? = null,
    val check: Int? = null,
    val access: Int? = null,
    val permitNotyfication: Int? = null,
    val permitSMS: Int? = null,
    val darkMode: Int? = null,
    val paczki: ArrayList<String>? = null,
    val token: String? = null)