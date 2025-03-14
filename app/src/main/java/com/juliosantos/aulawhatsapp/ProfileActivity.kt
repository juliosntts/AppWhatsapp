package com.juliosantos.aulawhatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.juliosantos.aulawhatsapp.databinding.ActivityProfileBinding
import com.juliosantos.aulawhatsapp.utils.showMessage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val authentication by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var cameraPermission = false
    private var galleryPermission = false

    private val managerGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadImageStorage(uri)
            binding.imgProfile.setImageURI(uri)
        } else {
            showMessage("Nenhuma imagem selecionada")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializateToolbar()
        requestPermissions()
        inicializateClickEvents()
    }

    override fun onStart() {
        super.onStart()
        recoverUserInitialData()
    }

    private fun recoverUserInitialData() {

        val userId = authentication.currentUser?.uid
        if( userId != null ){
            firestore
                .collection("users")
                .document( userId )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usersData = documentSnapshot.data
                    if( usersData != null ) {

                        val name = usersData["name"] as String
                        val photo = usersData["photo"] as String

                        binding.editProfileName.setText( name )
                        if ( photo.isNotEmpty() ) {
                            Picasso.get()
                                .load( photo )
                                .into( binding.imgProfile )
                        }
                    }
                }
        }
    }

    private fun inicializateClickEvents() {

        binding.fabSelect.setOnClickListener {
            if (galleryPermission) {
                managerGallery.launch("image/*")
            } else {
                showMessage("Não tem permissão para acessar galeria")
                requestPermissions()
            }
        }

        binding.btnSaveProfile.setOnClickListener {

            val username = binding.editProfileName.text.toString()
            if (username != null) {

                val userId = authentication.currentUser?.uid
                if (userId != null) {
                    val data = mapOf(
                        "name" to username
                    )
                    updateProfileData(userId, data)
                }
            } else {
                showMessage("Preencha o nome para atualizar")
            }
        }

    }

    private fun uploadImageStorage(uri: Uri) {

        val userId = authentication.currentUser?.uid

        if (userId != null) {
            storage.getReference("photos")
                .child("users")
                .child(userId)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { uri ->

                            val data = mapOf(
                                "photo" to uri.toString()
                            )
                            updateProfileData(userId, data)

                        }
                    showMessage("Sucesso ao fazer upload da imagem")
                }.addOnFailureListener {
                    showMessage("Erro ao fazer upload da imagem")
                }
        }

    }

    private fun updateProfileData(userId: String, data: Map<String, String>) {
        val userEmail = authentication.currentUser?.email ?: "Sem e-mail"

        // Adiciona o ID do usuário e o e-mail ao Firestore
        val updatedData = data.toMutableMap()
        updatedData["userId"] = userId
        updatedData["email"] = userEmail

        firestore.collection("users")
            .document(userId)
            .set(updatedData, com.google.firebase.firestore.SetOptions.merge()) // Cria ou atualiza
            .addOnSuccessListener {
                showMessage("Perfil atualizado com sucesso!")
            }
            .addOnFailureListener { e ->
                showMessage("Erro ao atualizar perfil: ${e.message}")
            }
    }

    private fun requestPermissions() {

        // checking permissions previously granted by the user
        cameraPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        galleryPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        // denied permissions list
        val listDeniedPermissions = mutableListOf<String>()
        if (!cameraPermission)
            listDeniedPermissions.add(Manifest.permission.CAMERA)
        if (!galleryPermission)
            listDeniedPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)

        if (listDeniedPermissions.isNotEmpty()) {
            // request multiple permissions
            val managerPermissions = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                //if a permission exists and is true, the value true will be set otherwise it uses the variable itself
                cameraPermission = permissions[Manifest.permission.CAMERA] ?: cameraPermission
                galleryPermission =
                    permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: galleryPermission
            }
            managerPermissions.launch(listDeniedPermissions.toTypedArray())
        }
    }


    private fun inicializateToolbar() {
        val toolbar = binding.tbProfile
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar pefil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}