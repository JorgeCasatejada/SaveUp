package com.example.saveup.view.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.saveup.view.mainScreen.AddTransaction
import com.example.saveup.R

class GroupDetailsExpensesFragment : Fragment() {

    private lateinit var btAdd: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_group_details_expenses, container, false)

        root.let {
            btAdd = it.findViewById(R.id.btAdd)
        }

        btAdd.setOnClickListener {
            startAddGroupTransaction()
        }

        return root
    }

    private fun startAddGroupTransaction() {
        val intent = Intent(requireContext(), AddTransaction::class.java)
        startActivityForResult(intent, 1)
    }
}