package com.example.dailyactivityregister

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DailyEntryAdapter(
    private val tasks: List<ProjectTask>,
    private val onProgressChanged: (String, Double) -> Unit
) : RecyclerView.Adapter<DailyEntryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_entry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onProgressChanged)
    }

    override fun getItemCount() = tasks.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskName: TextView = itemView.findViewById(R.id.taskName)
        private val progressInput: EditText = itemView.findViewById(R.id.progressInput)

        fun bind(task: ProjectTask, onProgressChanged: (String, Double) -> Unit) {
            taskName.text = task.name
            progressInput.hint = task.unit

            progressInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val progress = progressInput.text.toString().toDoubleOrNull() ?: 0.0
                    onProgressChanged(task.name, progress)
                }
            }
        }
    }
}