package com.example.saveup.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saveup.R
import com.example.saveup.model.firestore.FireParticipant

class ParticipantAdapter(
    private var participantsList: List<FireParticipant> = emptyList()
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    class ParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView = itemView.findViewById<ImageView>(R.id.participantImage)
        private val tvEmail = itemView.findViewById<TextView>(R.id.participantName)

        fun bindView(fireParticipant: FireParticipant) {

            tvEmail.text = fireParticipant.email
            val image = fireParticipant.imagePath.ifBlank { R.drawable.user_image }
            Glide.with(itemView)
                .load(image)
                .circleCrop()
                .into(imgView)
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
        holder.bindView(participantsList[position])
}