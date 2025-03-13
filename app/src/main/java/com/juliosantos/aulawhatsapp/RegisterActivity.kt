package com.juliosantos.aulawhatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.juliosantos.aulawhatsapp.databinding.ActivityRegisterBinding
import com.juliosantos.aulawhatsapp.model.User
import com.juliosantos.aulawhatsapp.utils.showMessage

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate( layoutInflater )
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    //Firebase
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializateToolbar()
        inicializateClickEvents()

    }

    private fun inicializateClickEvents() {
        binding.btnRegister.setOnClickListener {
            if( validateFields() ){
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { result ->
            if( result.isSuccessful ){

                val idUser = result.result.user?.uid
                if( idUser != null ){
                    val user = User(
                        idUser, name, email
                    )
                    saveUserFirestore( user )
                }

            }
        }.addOnFailureListener { error ->
            try {
                throw error
            }catch ( errorWeakPassword: FirebaseAuthWeakPasswordException ){
                errorWeakPassword.printStackTrace()
                showMessage("Senha fraca, digite outra com letras, número e caracteres especiais")
            }catch ( errorExistingUser: FirebaseAuthUserCollisionException ){
                errorExistingUser.printStackTrace()
                showMessage("E-mail já percente a outro usuário")
            }catch ( errorInvalidCredentials: FirebaseAuthInvalidCredentialsException ){
                errorInvalidCredentials.printStackTrace()
                showMessage("E-mail inválido, digite um outro e-mail")
            }
        }

    }

    private fun saveUserFirestore(user: User) {

        firestore
            .collection("usuarios")
            .document( user.id )
            .set( user )
            .addOnSuccessListener {
                showMessage("Sucesso ao fazer seu cadastro")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                showMessage("Erro ao fazer seu cadastro")
            }

    }

    private fun validateFields(): Boolean {

        name = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if( name.isNotEmpty() ){

            binding.textInputName.error = null
            if( email.isNotEmpty() ){

                binding.textInputEmail.error = null
                if( password.isNotEmpty() ){
                    binding.textInputPassword.error = null
                    return true
                }else{
                    binding.textInputPassword.error = "Preencha a senha"
                    return false
                }

            }else{
                binding.textInputEmail.error = "Preencha o seu e-mail!"
                return false
            }

        }else{
            binding.textInputName.error = "Preencha o seu nome!"
            return false
        }

    }

    private fun inicializateToolbar() {
        val toolbar = binding.tbRegister
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}