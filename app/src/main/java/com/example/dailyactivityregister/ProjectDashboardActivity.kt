package com.example.dailyactivityregister

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyactivityregister.databinding.ActivityProjectDashboardBinding
import kotlinx.coroutines.launch

class ProjectDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDashboardBinding
    private lateinit var project: Project
    private lateinit var db: AppDatabase
    private lateinit var adapter: ProjectTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        project = intent.getSerializableExtra("PROJECT") as Project
        title = project.project_name

        adapter = ProjectTaskAdapter(project.tasks) { task, progress ->
            task.current = progress
            // Refresh the specific item that was changed
            val index = project.tasks.indexOf(task)
            if (index != -1) {
                adapter.notifyItemChanged(index)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            db.projectDao().updateProject(project)
        }
    }
}