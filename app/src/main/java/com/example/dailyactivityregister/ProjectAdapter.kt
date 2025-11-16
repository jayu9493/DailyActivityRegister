package com.example.dailyactivityregister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.nameTextView.text = project.project_name
        holder.projectNumberTextView.text = project.project_number
        holder.taskCountTextView.text = "Tasks: ${project.tasks.size}"
        holder.itemView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount() = projects.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.projectName)
        val projectNumberTextView: TextView = itemView.findViewById(R.id.projectNumber)
        val taskCountTextView: TextView = itemView.findViewById(R.id.taskCount)
    }
}