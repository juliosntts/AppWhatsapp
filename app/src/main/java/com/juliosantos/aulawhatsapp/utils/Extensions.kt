package com.juliosantos.aulawhatsapp.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.juliosantos.aulawhatsapp.MainActivity

fun Activity.showMessage( mensagem: String ){
    Toast.makeText(
        this,
        mensagem,
        Toast.LENGTH_LONG
    ).show()
}

fun Activity.startMain( activity: Activity) {
    startActivity (
        Intent(applicationContext, MainActivity::class.java)
    )
}