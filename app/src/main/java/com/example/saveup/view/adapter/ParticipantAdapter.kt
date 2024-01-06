package com.example.saveup.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.saveup.R
import com.example.saveup.model.firestore.FireParticipant
import com.example.saveup.viewModel.MainViewModel

class ParticipantAdapter(
    private var viewModel: MainViewModel? = null,
    private var participantsList: List<FireParticipant> = emptyList(),
    private var isAdmin: Boolean = false
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    class ParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView = itemView.findViewById<ImageView>(R.id.participantImage)
        private val tvTitle = itemView.findViewById<TextView>(R.id.participantName)
        private val btDelete = itemView.findViewById<ImageButton>(R.id.deleteButton)

        fun bindView(fireParticipant: FireParticipant, isAdmin: Boolean, viewModel: MainViewModel?) {

            tvTitle.text = fireParticipant.email
            imgView.load(fireParticipant.imagePath) {
                crossfade(true)
                crossfade(500)
            }
            // Ajustar la visibilidad del botón de eliminación según si el usuario es administrador
            btDelete.visibility = if (isAdmin && !fireParticipant.isAdmin) View.VISIBLE else View.GONE

            btDelete.setOnClickListener {
                viewModel!!.deleteParticipantFromGroup(viewModel.currentGroup.value!!,fireParticipant.id)
            }
        }
    }

    fun update(participantsList: List<FireParticipant>) {
        this.participantsList = participantsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.line_recycler_view_participant, parent, false)
        )
    }

    override fun getItemCount(): Int = participantsList.size

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) =
        holder.bindView(participantsList[position], isAdmin, viewModel)

    fun setBtVisibilty(admin: Boolean) {
        isAdmin = admin
        notifyDataSetChanged()
    }
}