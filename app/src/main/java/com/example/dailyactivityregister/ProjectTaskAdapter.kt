package com.example.dailyactivityregister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

// This adapter now takes a map of calculated totals
class ProjectTaskAdapter(
    private var tasks: List<ProjectTask>,
    private var totals: Map<String, Double>
) : RecyclerView.Adapter<ProjectTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        val totalProgress = totals[task.name] ?: 0.0
        holder.bind(task, totalProgress)
    }

    override fun getItemCount() = tasks.size

    fun updateData(newTasks: List<ProjectTask>, newTotals: Map<String, Double>) {
        this.tasks = newTasks
        this.totals = newTotals
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val taskTarget: TextView = itemView.findViewById(R.id.taskTarget)
        private val taskCurrent: TextView = itemView.findViewById(R.id.taskCurrent)
        private val taskProgressBar: ProgressBar = itemView.findViewById(R.id.taskProgressBar)
        private val taskPercentage: TextView = itemView.findViewById(R.id.taskPercentage)

        private val percentageFormat = DecimalFormat("##0.##%")

        fun bind(task: ProjectTask, totalProgress: Double) {
            taskName.text = task.name
            taskTarget.text = "/ ${task.target} ${task.unit}"
            taskCurrent.text = totalProgress.toString()

            val progress = if (task.target > 0) (totalProgress / task.target) else 0.0
            taskProgressBar.progress = (progress * 100).toInt()
            taskPercentage.text = "(${percentageFormat.format(progress)})"
        }
    }
}