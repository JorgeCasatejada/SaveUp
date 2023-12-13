package com.example.saveup.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveup.GroupAdapter
import com.example.saveup.MainViewModel
import com.example.saveup.R
import com.example.saveup.databinding.FragmentGroupsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Group


class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    val INTENT_ADD_Group: Int = 1

    private var account: Account? = null

    private var viewModel: MainViewModel? = null

    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]


        binding.recyclerGroups.layoutManager = LinearLayoutManager(context)
        binding.recyclerGroups.setHasFixedSize(true)
        groupAdapter = GroupAdapter { showGroup(it) }
        binding.recyclerGroups.adapter = groupAdapter

        if (!viewModel!!.userGroups.isInitialized) {
            viewModel!!.getUserGroups()
            binding.recyclerGroups.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.fabAddGroup.setOnClickListener {
            startAddGroupActivity()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.userGroups?.observe(viewLifecycleOwner) {
            groupAdapter.update(it)
            binding.progressBar.visibility = View.GONE
            binding.recyclerGroups.visibility = View.VISIBLE
        }
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