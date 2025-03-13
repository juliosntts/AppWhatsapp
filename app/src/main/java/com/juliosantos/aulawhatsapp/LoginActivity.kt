package com.juliosantos.aulawhatsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.juliosantos.aulawhatsapp.databinding.ActivityLoginBinding
import com.juliosantos.aulawhatsapp.utils.showMessage

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private lateinit var email: String
    private lateinit var password: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializateClickEvents()

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInUser()
    }

    private fun checkLoggedInUser() {
        val currentUser = firebaseAuth.currentUser
        if( currentUser != null ){
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun inicializateClickEvents() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
        binding.btnLogin.setOnClickListener {
            if( validateFields() ){
                loginUser()
            }
        }

    }

    private fun loginUser() {

        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            showMessage("Logado com sucesso!")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { error ->

            try {
                throw error
            }catch ( errorInvalidUser: FirebaseAuthInvalidUserException){
                errorInvalidUser.printStackTrace()
                showMessage("E-mail não cadastrado")
            }catch ( errorInvalidCredential: FirebaseAuthInvalidCredentialsException){
                errorInvalidCredential.printStackTrace()
                showMessage("E-mail ou senha estão incorretos!")
            }

        }

    }

    private fun validateFields(): Boolean {

        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        if( email.isNotEmpty() ){

            binding.textInputLoginEmail.error = null
            if( password.isNotEmpty() ){
                binding.textInputLoginPassword.error = null
                return true
            }else{
                binding.textInputLoginPassword.error = "Preencha o e-mail"
                return false
            }

        }else{//Está vazio
            binding.textInputLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}