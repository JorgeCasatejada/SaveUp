package com.example.saveup.view.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
        groupAdapter = GroupAdapter(onItemSelected = { group ->
            // Asegúrate de que esta función esté implementada correctamente
            // y que no genere excepciones o errores.
            // Puedes agregar un log o un punto de interrupción para verificar.
            // También puedes intentar imprimir un mensaje de log para debug.
            // Luego, abre el nuevo fragmento aquí.
            val otroFragmento = GroupDetailsFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, otroFragmento)
                .addToBackStack(null)
                .commit()
        })
        binding.recyclerGroups.adapter = groupAdapter

        if (!viewModel!!.userGroups.isInitialized) {
            viewModel!!.getUserGroups()
            binding.recyclerGroups.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.fabAddGroup.setOnClickListener {
            startAddGroupActivity()
        }

        viewModel!!.userGroups.observe(viewLifecycleOwner,  Observer {
            groupAdapter.update(it)
        })

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
                }
                if (group != null) {
                    viewModel?.addAdminToGroup(group, viewModel?.getUserEmail().toString())
                }
                participants?.forEach { participant ->
                    if (group != null) {
                        viewModel?.addParticipantToGroup(group, participant)
                    }
                }
            }

        }
    }


    private fun showGroup(group: Group) {
        val details = GroupDetailsFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, details)
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