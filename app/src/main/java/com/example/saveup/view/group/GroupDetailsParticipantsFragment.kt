package com.example.saveup.view.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveup.databinding.FragmentGroupDetailsParticipantsBinding
import com.example.saveup.view.adapter.ParticipantAdapter
import com.example.saveup.viewModel.MainViewModel

class GroupDetailsParticipantsFragment : Fragment() {

    private var _binding: FragmentGroupDetailsParticipantsBinding? = null
    private val binding get() = _binding!!
    private var viewModel: MainViewModel? = null

    private lateinit var participantAdapter: ParticipantAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDetailsParticipantsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.recyclerParticipants.layoutManager = LinearLayoutManager(context)
        binding.recyclerParticipants.setHasFixedSize(true)
        participantAdapter = ParticipantAdapter()
        binding.recyclerParticipants.adapter = participantAdapter

        viewModel!!.currentGroupParticipants.observe(viewLifecycleOwner,  Observer {
            participantAdapter.update(it)
        })

        binding.btDeleteGroup.setOnClickListener {
            //TODO: Falta borrar de la lista de grupos del usuario
            viewModel!!.deleteGroup(viewModel!!.getCurrentGroup()!!)
        }

        binding.btClose.setOnClickListener {
            closeGroup()
        }

        binding.btExitGroup.setOnClickListener {
            viewModel!!.deleteParticipantFromGroup(viewModel!!.getCurrentGroup()!!,
                viewModel!!.getUserEmail())
            closeGroup()
        }

        binding.btAddParticipant.setOnClickListener {
            viewModel!!.addParticipantToGroup(viewModel!!.getCurrentGroup()!!,
                binding.etIdParticipant.text.toString())
            closeGroup()
        }

        showMode()

        return binding.root
    }

    private fun closeGroup() {
        requireActivity().supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun showMode() {
        //TODO: función para saber si es admin
        updateVisibility(false)
    }

    private fun updateVisibility(isAdmin: Boolean) {
        binding.addParticipant.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.btExitGroup.visibility = if (!isAdmin) View.VISIBLE else View.GONE
        binding.btDeleteGroup.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

}