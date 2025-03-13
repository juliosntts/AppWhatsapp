package com.juliosantos.aulawhatsapp.model

data class User(
    var id: String,
    var name: String,
    var email: String,
    var photo: String = ""
)
