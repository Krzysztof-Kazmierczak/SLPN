package com.example.slpn.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.slpn.databinding.FragmentLoginBinding
import com.example.slpn.fragments.register.RegisterViewModel
import com.example.slpn.fragments.repository.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : BaseFragment() {

    private val fbAuth = FirebaseAuth.getInstance()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val regVm by viewModels<RegisterViewModel>()
    private val LOG_DEUBG = "LOG_DEBUG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginClick()
        setupRegistrationClick()
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
    //Przejście do fragmentu z tworzeniem nowego użytkownika
    private fun setupRegistrationClick() {
        binding.RegisterButton.setOnClickListener {
            findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment().actionId)
        }
    }
    //Próba zalogowania do aplikacji
    private fun setupLoginClick() {
        binding.LoginButton.setOnClickListener {
            //Pobranie informacji z pól tekstowych
            val email = binding.LogMail.editText!!.text.toString().trim()
            val pass = binding.LogPass.editText!!.text.toString().trim()
            if(email.isNotEmpty() and pass.isNotBlank())
            {
                //Wysłanie do bazy danych czy login i hasło są prawidłowe
                fbAuth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener { authRes ->
                        //Jeżeli wszystko się zgadza logujemy się do aplikacji
                        if(authRes.user != null) startApp()
                    }
                    .addOnFailureListener{ exc ->
                        //Jeżeli występuje błąd wyświetlamy komunikat o błędzie użytkownikowi
                        Snackbar.make(requireView(), "Błędny login lub hasło", Snackbar.LENGTH_SHORT)
                            .show()
                        Log.d(LOG_DEUBG, exc.message.toString())
                    }
            }
            else{
                if(email.isEmpty()){
                    binding.LogMail.helperText = "Field can't be empty"
                }
                if(pass.isEmpty()){
                    binding.LogPass.helperText = "Field can't be empty"
                }
            }

        }
    }
}