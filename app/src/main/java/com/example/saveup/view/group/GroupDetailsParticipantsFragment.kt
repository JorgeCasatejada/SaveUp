package com.example.saveup.view.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveup.databinding.FragmentGroupDetailsParticipantsBinding
import com.example.saveup.view.adapter.ParticipantAdapter
import com.example.saveup.viewModel.MainViewModel

class GroupDetailsParticipantsFragment : Fragment() {

    private var _binding: FragmentGroupDetailsParticipantsBinding? = null
    private val binding get() = _binding!!
    private var viewModel: MainViewModel? = null
    private var showMessage = false
    private var isFragmentVisible = false

    private lateinit var participantAdapter: ParticipantAdapter

    override fun onResume() {
        super.onResume()
        isFragmentVisible = true
        // Ahora que el fragmento está visible, se puede mostrar el Toast si es necesario
        showMessage = true
    }

    override fun onPause() {
        super.onPause()
        isFragmentVisible = false
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
        participantAdapter = ParticipantAdapter(onItemSelected = { participant ->
            viewModel!!.deleteParticipantFromGroup(viewModel!!.currentGroup.value!!, participant.id)
        })
        binding.recyclerParticipants.adapter = participantAdapter

        viewModel!!.currentGroupParticipants.observe(viewLifecycleOwner) {
            participantAdapter.update(it)
            viewModel!!.checkAdmin(it)
        }

        viewModel!!.isGroupAdmin.observe(viewLifecycleOwner) {
            updateVisibility(it)
        }

        binding.btDeleteGroup.setOnClickListener {
            viewModel!!.deleteGroup(viewModel!!.getCurrentGroup()!!)
        }

        binding.btClose.setOnClickListener {
            closeGroup()
        }

        binding.btExitGroup.setOnClickListener {
            viewModel!!.exitFromCurrentGroup()
            closeGroup()
        }

        binding.btAddParticipant.setOnClickListener {
            viewModel!!.addParticipantToGroup(
                viewModel!!.getCurrentGroup()!!,
                binding.etIdParticipant.text.toString()
            )
            binding.etIdParticipant.text = null
        }

        showMessage = false

        viewModel!!.participantAddedResult.observe(this) { result ->
            val (success, message) = result
            if (success && showMessage) {
                Toast.makeText(context, "Se ha añadido al usuario: $message", Toast.LENGTH_SHORT)
                    .show()
            } else if (!success && showMessage) {
                Toast.makeText(
                    context,
                    "No se ha podido añadir al usuario: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    private fun closeGroup() {
        requireActivity().supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun updateVisibility(isAdmin: Boolean) {
        binding.addParticipant.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.btExitGroup.visibility = if (!isAdmin) View.VISIBLE else View.GONE
        binding.btDeleteGroup.visibility = if (isAdmin) View.VISIBLE else View.GONE
        participantAdapter.setBtVisibilty(isAdmin)
    }

}