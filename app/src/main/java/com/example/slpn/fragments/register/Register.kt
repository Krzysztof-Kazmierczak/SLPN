package com.example.slpn.fragments.register

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.slpn.databinding.FragmentRegisterBinding
import com.example.slpn.fragments.repository.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class Register : BaseFragment() {
    //Pattern hasła. Minimalne wymagania co musi posiadać hasło. Jedna cyfra, jedna duża litera, brak spacji, min. 6 znaków
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                //"(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 6 characters
                "$"
    )

    private val REG_DEBUG = "REG_DEBUG"
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val fbAuth = FirebaseAuth.getInstance()
    private val regVm by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmInput()
        //Sprawdzanie połączenia z internetem
        observeInternetConnection()
    }
    //Sprawdzanie połączenia z internetem
    override fun onResume() {
        super.onResume()
        regVm.checkInternetConnection(requireActivity().application)
    }
    //Sprawdzanie połączenia z internetem
    private fun observeInternetConnection(){
        regVm.isConnectedToTheInternet.observe(viewLifecycleOwner){
            it?.let{
                binding.networkConnection.visibility = if(it) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //Pobranie informacji z pól tekstowych
    private fun setupSignUpClick() {
        val email = binding.RegMail.editText!!.text.toString().trim()
        val pass = binding.RegPass.editText!!.text.toString().trim()
        val phoneNumber = binding.RegNumPhon.editText!!.text.toString().trim()
        //Stworzenie nowego użytkownika w bazie danych za pomocą emiala i hasła
        fbAuth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authRes ->

                //NOWY UZYTKOWNIK Z POTWIERDZENIEM NA MAILA! (FUNKCJA GOTOWA DO UŻYCIA)
                /* var userMail = fbAuth.currentUser
                 userMail?.sendEmailVerification()
                     ?.addOnSuccessListener {
                         //Toast.makeText(requireContext(), "Potwierdz adres Email", Toast.LENGTH_SHORT).show()
                 }*/
                //Stworzenie w bazie danych dokumentu dla użytkownika
                if (authRes.user != null) {
                    val user = com.example.slpn.data.User(
                        authRes.user!!.uid,
                        "",
                        "",
                        authRes.user!!.email,
                        "",
                        phoneNumber,
                        0,
                        0,
                        1,
                        1,
                        0,
                        arrayListOf(),
                    )
                    regVm.createNewUser(user)
                    token()
                    startApp()
                }
            }
            //Jeżeli wystąpił problem wyświetlamy informacjię użytkownikowi że coś poszło nie tak.
            .addOnFailureListener { exc ->
                Snackbar.make(
                    requireView(),
                    "Upss...Something went wrong...",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                Log.d(REG_DEBUG, exc.message.toString())
            }
    }
    //Sprawdzenie czy wszystkie wymagania rejestracyjne zostały spełnione
    fun confirmInput() {
        binding.buttonCreate.setOnClickListener {
            if (!validateEmail() or !validatePhone() or !validatePassword() or !validatePasswordRepeat()) {
                return@setOnClickListener
            }
            var input = "Email: " + binding.RegMail?.getEditText()?.getText().toString()
            input += "\n"
            input += "Phone Number: " + binding.RegNumPhon?.getEditText()?.getText().toString()
            Toast.makeText(requireContext(), input, Toast.LENGTH_SHORT).show()
            setupSignUpClick()
        }
    }
    //Wymaganie rejsetracyjne - poprawny email (Automatyczna funkacja sprawdzająca czy to co wpisał użytkownik jest mailem czy nie
    private fun validateEmail(): Boolean {
        //Sprawdzenie czy to pole nie zostało puste jeżeli tak wyświetlany jest komunikat
        return if (binding.RegMail.editText!!.text.toString().trim().isEmpty()) {
            binding.RegMail.helperText = "Field can't be empty"
            false
            //Jeżeli wpisany tekst nie jest mailem wyświetlamy komunikat
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.RegMail.editText!!.text.toString().trim()).matches()) {
            binding.RegMail.helperText = "Please enter a valid email address"
            false
        } else {
            //Wszystko się zgadza. Warunek jest spełniony
            binding.RegMail.helperText = null
            true
        }
    }
    //Wymaganie rejsetracyjne - poprawny numer telefonu. Liczba cyfr = 9
    private fun validatePhone(): Boolean {
        //Sprawdzenie czy to pole nie zostało puste jeżeli tak wyświetlany jest komunikat
        return if (binding.RegNumPhon?.editText!!.text.toString().trim().isEmpty()) {
            binding.RegNumPhon.helperText = "Field can't be empty"
            false
            //Jeżeli wpisany tekst nie ma 9 cyfr wyświetlamy komunikat
        } else if (binding.RegNumPhon?.editText!!.text.toString().trim().length != 9) {
            binding.RegNumPhon.helperText = "Please enter a valid phone number"
            false
        } else {
            //Wszystko się zgadza. Warunek jest spełniony
            binding.RegNumPhon.helperText = null
            true
        }
    }
    //Wymaganie rejsetracyjne - poprawne hasło. USTAWIAMY W PATTERNIE (początek tego fragmentu)
    // Jedna cyfra, jedna duża litera, brak spacji, min. 6 znaków
    private fun validatePassword(): Boolean {
        //Sprawdzenie czy to pole nie zostało puste jeżeli tak wyświetlany jest komunikat
        val passwordInput = binding.RegPass?.editText!!.text.toString().trim()
        return if (passwordInput.isEmpty()) {
            binding.RegPass.helperText = "Field can't be empty"
            false
            //Jeżeli pattern nie jest sprawdzony wyświetlamy komunikat o wymaganiach hasła
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            binding.RegPass.helperText = "Password too weak. Use: 1 lower letter, 1 upper letter, 1 digit, at least 6 characters, no spaces"
            false
        } else {
            //Wszystko się zgadza. Warunek jest spełniony
            binding.RegPass.helperText = null
            true
        }
    }
    //Wymaganie rejsetracyjne - poprawne powtórzenie hasła.
    private fun validatePasswordRepeat(): Boolean {
        //Sprawdzenie czy to pole nie zostało puste jeżeli tak wyświetlany jest komunikat
        val passwordRepeatInput = binding.RegPass?.editText!!.text.toString().trim()
        val passwordInput = binding.RegPassRep?.editText!!.text.toString().trim()
        return if (passwordInput.isEmpty()){
            binding.RegPassRep.helperText = "Field can't be empty"
            false
            //Jeżeli hasła się nie pokrywają wyświetlamy komunikat
        } else if (passwordInput != passwordRepeatInput) {
            binding.RegPassRep.helperText = "The passwords entered do not match"
            false
        } else {
            //Wszystko się zgadza. Warunek jest spełniony
            binding.RegPassRep.helperText = null
            true
        }
    }
}