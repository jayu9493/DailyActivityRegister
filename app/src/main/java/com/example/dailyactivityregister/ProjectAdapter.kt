package com.example.dailyactivityregister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(private val projects: List<Project>, private val onItemClick: (Project) -> Unit) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.nameTextView.text = project.name
        holder.agencyTextView.text = project.agency
        holder.activityCountTextView.text = "Activities: ${project.activities.size}"
        holder.itemView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount() = projects.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.projectName)
        val agencyTextView: TextView = itemView.findViewById(R.id.projectAgency)
        val activityCountTextView: TextView = itemView.findViewById(R.id.activityCount)
    }
}
