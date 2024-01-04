package com.example.saveup.view.group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.R
import com.example.saveup.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class GroupDetailsFragment : Fragment() {

    private lateinit var navView: BottomNavigationView
    private var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return inflater.inflate(R.layout.fragment_group_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navView = view.findViewById(R.id.nav_view)
        cargarMenu()
        mostrarTransacciones()

        var registerListeners = true
        viewModel!!.currentGroup.observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d("GroupDetailsFragment", "El grupo seleccionado no existe")
                viewModel!!.deleteGroupFromMyGroups()
                showGroupDialog(true)
            } else {
                if (it.id.isNotBlank()) {
                    if (registerListeners) {
                        registerListeners = false
                        viewModel!!.registerGroupParticipantsListener(it)
                        viewModel!!.registerGroupTransactionsListener(it)
                    }
                } else {
                    Log.d("GroupDetailsFragment", "Abierto el grupo vacio")
                }
            }
        }
        viewModel!!.currentGroupParticipants.observe(viewLifecycleOwner) {
            Log.d(
                "GroupDetailsFragment",
                "Comprobar si sigo perteneciendo al grupo o se me ha expulsado"
            )
            if (!viewModel!!.checkUserStillInGroup(it)) {
                viewModel!!.exitFromCurrentGroup()
                showGroupDialog(false)
                Log.d("GroupDetailsFragment", "Se me ha expulsado del grupo")
            } else {
                Log.d("GroupDetailsFragment", "Sigo en el grupo")
            }
        }
    }

    private fun showGroupDialog(case: Boolean) {
        val dialogFragment = GroupDialog.newInstance(case)
        dialogFragment.show(requireActivity().supportFragmentManager, "GroupDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel!!.setDefaultGroupValues()
        viewModel!!.unregisterGroupInfoListener()
        viewModel!!.unregisterGroupParticipantsListener()
        viewModel!!.unregisterGroupTransactionsListener()
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
