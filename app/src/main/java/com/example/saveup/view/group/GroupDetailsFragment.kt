package com.example.saveup.view.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.saveup.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.viewModel.MainViewModel

class GroupDetailsFragment : Fragment() {

    private lateinit var navView: BottomNavigationView
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navView = view.findViewById(R.id.nav_view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        cargarMenu()
        mostrarTransacciones()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun mostrarParticipantes() {
        val participantsFragment = GroupDetailsParticipantsFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, participantsFragment)
            .commit()
    }

    private fun mostrarTransacciones() {
        val transactionsFragment = GroupDetailsExpensesFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, transactionsFragment)
            .commit()
    }

    private fun cargarMenu() {
        // Esto es el listener.
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
