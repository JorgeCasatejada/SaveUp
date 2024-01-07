package com.example.saveup.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saveup.R
import com.example.saveup.model.firestore.FireParticipant

class ParticipantAdapter(
    private val onItemSelected: (FireParticipant) -> Unit,
    private var participantsList: List<FireParticipant> = emptyList(),
    private var isAdmin: Boolean = false
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    class ParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView = itemView.findViewById<ImageView>(R.id.participantImage)
        private val tvEmail = itemView.findViewById<TextView>(R.id.participantName)
        private val btDelete = itemView.findViewById<ImageButton>(R.id.deleteButton)

        fun bindView(fireParticipant: FireParticipant, isAdmin: Boolean, onItemSelected: (FireParticipant) -> Unit) {

            tvEmail.text = fireParticipant.email
            val image = fireParticipant.imagePath.ifBlank { R.drawable.user_image }
            Glide.with(itemView)
                .load(image)
                .circleCrop()
                .into(imgView)

            // Ajustar la visibilidad del botón de eliminación según si el usuario es administrador
            btDelete.visibility = if (isAdmin && !fireParticipant.isAdmin) View.VISIBLE else View.GONE

            btDelete.setOnClickListener {
                onItemSelected(fireParticipant)
            }
        }
    }

    fun update(participantsList: List<FireParticipant>) {
        this.participantsList = participantsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.line_recycler_view_participant, parent, false)
        )
    }

    override fun getItemCount(): Int = participantsList.size

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) =
        holder.bindView(participantsList[position], isAdmin, onItemSelected)

    fun setBtVisibilty(admin: Boolean) {
        isAdmin = admin
        notifyDataSetChanged()
    }
}