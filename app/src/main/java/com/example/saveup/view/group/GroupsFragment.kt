package com.example.saveup.view.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveup.R
import com.example.saveup.databinding.FragmentGroupsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Group
import com.example.saveup.view.adapter.GroupAdapter
import com.example.saveup.viewModel.MainViewModel

class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    val INTENT_ADD_GROUP: Int = 1

    private var account: Account? = null

    private var viewModel: MainViewModel? = null

    private lateinit var groupAdapter: GroupAdapter

    private var showMessage = false
    private var isFragmentVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }
    }

    override fun onResume() {
        super.onResume()
        isFragmentVisible = true
        // Ahora que el fragmento está visible, se puede mostrar el Toast si es necesario
        showMessage = true
        viewModel!!.registerUserGroupsListener()
    }

    override fun onPause() {
        super.onPause()
        isFragmentVisible = false
        viewModel!!.unregisterUserGroupsListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]


        binding.recyclerGroups.layoutManager = LinearLayoutManager(context)
        binding.recyclerGroups.setHasFixedSize(true)
        groupAdapter = GroupAdapter(onItemSelected = { group ->
            viewModel!!.registerGroupInfoListener(group)
            showGroup()
        })
        binding.recyclerGroups.adapter = groupAdapter

        if (!viewModel!!.userGroups.isInitialized) {
            binding.recyclerGroups.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.fabAddGroup.setOnClickListener {
            startAddGroupActivity()
        }

        showMessage = false

        viewModel!!.participantsNotAddedResult.observe(this) { result ->
            if (showMessage) {
                var message = ""
                for (p in result)
                    message += "$p, "
                message = message.dropLast(2)
                Toast.makeText(
                    context,
                    "No se ha podido añadir a los usuarios: $message",
                    Toast.LENGTH_LONG
                ).show()
                showMessage = false
            }
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
        startActivityForResult(intent, INTENT_ADD_GROUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Maneja el resultado aquí
        if (requestCode == INTENT_ADD_GROUP) {
            if (resultCode == Activity.RESULT_OK) {
                val group = data?.getParcelableExtra<Group>(AddGroupActivity.CREATED_GROUP)
                val participants = data?.getStringArrayListExtra(AddGroupActivity.PARTICIPANTS)
                if (group != null) {
                    viewModel?.createGroup(group)
                    viewModel?.addAdminToGroup(group, viewModel?.getUserEmail().toString())
                    if (!participants.isNullOrEmpty()) {
                        viewModel?.addParticipantsToGroup(group, participants)
                    }
                }
            }
        }
    }


    private fun showGroup() {
        val details = GroupDetailsFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, details)
            .addToBackStack(null)
            .commit()
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