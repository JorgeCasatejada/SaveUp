package com.example.saveup.view.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.saveup.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class GroupDetailsFragment : Fragment() {

    lateinit var navView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navView = view.findViewById(R.id.nav_view)
        cargarMenu()
        mostrarParticipantes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun mostrarParticipantes() {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GroupDetailsParticipantsFragment())
            .commit()
    }

    private fun mostrarTransacciones() {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GroupDetailsExpensesFragment())
            .commit()
    }

    private fun cargarMenu() {
        // Esto es el listener. Recuerda, el when es similar al switch.
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