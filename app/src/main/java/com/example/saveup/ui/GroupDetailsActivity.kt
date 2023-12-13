package com.example.saveup.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.saveup.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class GroupDetailsActivity : AppCompatActivity() {

    lateinit var navView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        navView = findViewById(R.id.nav_view)
        cargarMenu()
        mostrarParticipantes()
    }


    private fun mostrarParticipantes() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GroupDetailsParticipantsFragment())
            .commit()
    }

    private fun mostrarTransacciones() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GroupDetailsExpensesFragment())
            .commit()
    }

    private fun cargarMenu() {
        //Esto es el listener. Recuerda, el when es similar al switch.
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_transactions -> {
                    mostrarTransacciones()
                }
                R.id.navigation_participants -> {
                    mostrarParticipantes()
                }
            }
            true
        }
    }

}