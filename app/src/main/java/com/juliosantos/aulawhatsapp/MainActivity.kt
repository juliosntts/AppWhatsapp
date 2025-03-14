package com.juliosantos.aulawhatsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.juliosantos.aulawhatsapp.adapters.ViewPagerAdapter
import com.juliosantos.aulawhatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializateToolbar()
        initializeTabbedNavigation()

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_profile -> {
                        startActivity(
                            Intent(applicationContext, ProfileActivity::class.java)
                        )
                    }

                    R.id.item_exit -> {
                        logoutUser()
                    }
                }
                return true
            }
        }
        ) // support for the selected item in the menu

    }

    private fun initializeTabbedNavigation() {

        val tabLayout = binding.tabLayoutMain
        val viewPager = binding.viewPagerMain

        //Adapter
        val tabs = listOf("CONVERSAS", "CONTATOS")
        viewPager.adapter = ViewPagerAdapter(tabs, supportFragmentManager, lifecycle)

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

    } // Start browsing through conversation and contact tabs

    private fun logoutUser() {

        AlertDialog.Builder(this).setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não") { _, _ -> }
            .setPositiveButton("Sim") { _, _ ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }
            .create()
            .show()

    } // Function to log out the user

    private fun inicializateToolbar() {
        val toolbar = binding.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Whatsapp"
        }
    }

}