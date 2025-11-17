package com.example.dailyactivityregister

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

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
        holder.bind(task, onProgressChanged)
    }

    override fun getItemCount() = tasks.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val taskTarget: TextView = itemView.findViewById(R.id.taskTarget)
        private val taskCurrent: EditText = itemView.findViewById(R.id.taskCurrent)
        private val taskProgressBar: ProgressBar = itemView.findViewById(R.id.taskProgressBar)
        private val taskPercentage: TextView = itemView.findViewById(R.id.taskPercentage)

        private val percentageFormat = DecimalFormat("##0.##%")
        private var textWatcher: TextWatcher? = null

        fun bind(task: ProjectTask, onProgressChanged: (ProjectTask, Double) -> Unit) {
            taskName.text = task.name
            taskTarget.text = "/ ${task.target} ${task.unit}"

            // Remove the old listener before setting new text
            textWatcher?.let { taskCurrent.removeTextChangedListener(it) }
            taskCurrent.setText(task.current.toString())

            // Calculate and set progress
            val progress = if (task.target > 0) (task.current / task.target) else 0.0
            taskProgressBar.progress = (progress * 100).toInt()
            taskPercentage.text = "(${percentageFormat.format(progress)})"

            // Add the new listener
            textWatcher = taskCurrent.addTextChangedListener {
                val newProgress = it.toString().toDoubleOrNull() ?: 0.0
                onProgressChanged(task, newProgress)
            }
        }
    }
}