package com.example.saveup.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saveup.R
import com.example.saveup.model.Group

class GroupAdapter(
    private var groupList: List<Group> = emptyList(),
    private val onItemSelected: (Group) -> Unit,
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView = itemView.findViewById<ImageView>(R.id.imageViewIcon)
        private val tvTitle = itemView.findViewById<TextView>(R.id.titleGroup)
        private val rootView = itemView.rootView

        fun bindView(group: Group, onItemSelected: (Group) -> Unit) {

            tvTitle.text = group.title
            val image = group.urlGroupImage.ifBlank { R.drawable.baseline_groups_24 }
            Glide.with(itemView)
                .load(image)
                .circleCrop()
                .into(imgView)

            //Listener
            rootView.setOnClickListener { onItemSelected(group) }
        }
    }

    fun update(groupList: List<Group>) {
        this.groupList = groupList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.line_recycler_view_group, parent, false)
        )
    }

    override fun getItemCount(): Int = groupList.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) =
        holder.bindView(groupList[position], onItemSelected)
}