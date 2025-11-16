package com.example.dailyactivityregister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class ProjectTaskAdapter(
    private val tasks: List<ProjectTask>,
    private val onProgressChanged: (ProjectTask, Double) -> Unit
) : RecyclerView.Adapter<ProjectTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.taskTarget.text = "/ ${task.target} ${task.unit}"
        holder.taskCurrent.setText(task.current.toString())

        holder.taskCurrent.addTextChangedListener {
            val progress = it.toString().toDoubleOrNull() ?: 0.0
            onProgressChanged(task, progress)
        }
    }

    override fun getItemCount() = tasks.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val taskTarget: TextView = itemView.findViewById(R.id.taskTarget)
        val taskCurrent: EditText = itemView.findViewById(R.id.taskCurrent)
    }
}