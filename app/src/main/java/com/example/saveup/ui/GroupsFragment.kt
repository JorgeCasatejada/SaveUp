package com.example.saveup.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saveup.GroupAdapter
import com.example.saveup.R
import com.example.saveup.model.Account
import com.example.saveup.model.Group
import com.google.android.material.floatingactionbutton.FloatingActionButton


class GroupsFragment : Fragment() {
    val INTENT_ADD_Group: Int = 1

    private var account: Account? = null

    private lateinit var recyclerViewGroups : RecyclerView

    private lateinit var groupAdapter : GroupAdapter

    private lateinit var btAddGroup : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_groups, container, false)

        recyclerViewGroups = root.findViewById(R.id.recyclerGroups)
        recyclerViewGroups.layoutManager = LinearLayoutManager(context)
        recyclerViewGroups.setHasFixedSize(true)
        groupAdapter = GroupAdapter{showGroup(it)}
        recyclerViewGroups.adapter = groupAdapter
        groupAdapter.update(account?.groups ?: emptyList())

        root.let {
            btAddGroup = it.findViewById(R.id.fabAddGroup)
        }

        btAddGroup.setOnClickListener {
            startAddGroupActivity()
        }

        return root
    }

    private fun startAddGroupActivity() {
        val intent = Intent(requireContext(), AddGroupActivity::class.java)
        startActivityForResult(intent, INTENT_ADD_Group)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Maneja el resultado aquí
        if (requestCode == INTENT_ADD_Group) {
            if (resultCode == Activity.RESULT_OK) {

                groupAdapter.update(account?.groups ?: emptyList())
            }

        }
    }*/


    private fun showGroup(group: Group) {
        val intent = Intent(requireContext(), GroupDetailsActivity::class.java)
        startActivityForResult(intent, INTENT_ADD_Group)
    }

    companion object {
        private const val ACCOUNT = "Account"
        @JvmStatic
        fun newInstance(account: Account?): GroupsFragment {
            val fragment = GroupsFragment()
            val args = Bundle()
            args.putParcelable(ACCOUNT, account)
            fragment.arguments = args
            return fragment
        }
    }
}