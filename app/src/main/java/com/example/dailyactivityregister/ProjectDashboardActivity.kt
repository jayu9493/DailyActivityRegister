package com.example.dailyactivityregister

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyactivityregister.databinding.ActivityProjectDashboardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProjectDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDashboardBinding
    private var project: Project? = null
    private lateinit var db: AppDatabase
    private lateinit var adapter: ProjectTaskAdapter
    private var projectName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("PROJECT", Project::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("PROJECT") as? Project
        }

        project?.let {
            projectName = it.project_name
            setupDashboard(it)
            setupSwipeRefresh()
            // Automatically refresh data when opening the dashboard
            refreshProjectData()
        } ?: run {
            Toast.makeText(this, "Error: Project data is missing.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshProjectData()
        }
    }

    private fun refreshProjectData() {
        lifecycleScope.launch {
            try {
                // Fetch fresh data from server
                val updatedProject = com.example.dailyactivityregister.network.RetrofitInstance.api.getProjects()
                    .find { it.project_name == projectName }
                
                if (updatedProject != null) {
                    project = updatedProject
                    setupDashboard(updatedProject)
                    Toast.makeText(this@ProjectDashboardActivity, "Refreshed!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProjectDashboardActivity, "Project not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProjectDashboardActivity, "Refresh failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setupDashboard(p: Project) {
        title = p.project_name
        binding.commencementDate.text = "Commencement: ${formatDate(p.commencement_date)}"
        binding.totalRoute.text = String.format(Locale.US, "Total Route: %.3f km", (p.oh_line_length + p.ug_line_length))
        binding.villages.text = "Villages: ${p.line_passing_villages ?: "N/A"}"

        val totals = calculateTotals(p)
        
        // Initialize or update adapter
        if (::adapter.isInitialized) {
            adapter.updateData(p.tasks, totals)
        } else {
            adapter = ProjectTaskAdapter(p.tasks, totals)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
        }

        binding.addDailyEntryButton.setOnClickListener {
            showAddDailyEntryDialog()
        }
        
        binding.deleteProjectButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val passwordInput = EditText(this)
        passwordInput.hint = "Enter password"
        passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Project")
            .setMessage("Are you sure you want to delete this project? This action cannot be undone.\\n\\nEnter password to confirm:")
            .setView(passwordInput)
            .setPositiveButton("Delete") { _, _ ->
                val password = passwordInput.text.toString()
                deleteProject(password)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProject(password: String) {
        lifecycleScope.launch {
            try {
                val response = com.example.dailyactivityregister.network.RetrofitInstance.api.deleteProject(projectName, password)
                Toast.makeText(this@ProjectDashboardActivity, response["message"] ?: "Project deleted", Toast.LENGTH_SHORT).show()
                
                // Close the activity and return to main screen
                finish()
            } catch (e: retrofit2.HttpException) {
                val errorMessage = when (e.code()) {
                    403 -> "Invalid password"
                    404 -> "Project not found"
                    else -> "Failed to delete project: ${e.message()}"
                }
                Toast.makeText(this@ProjectDashboardActivity, errorMessage, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProjectDashboardActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAddDailyEntryDialog() {
        project?.let { currentProject ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_daily_entry, null)
            val dailyEntryRecyclerView = dialogView.findViewById<RecyclerView>(R.id.dailyEntryRecyclerView)
            val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)

            val newProgress = mutableMapOf<String, Double>()
            currentProject.tasks.forEach { newProgress[it.name] = 0.0 }

            val dailyAdapter = DailyEntryAdapter(currentProject.tasks) { taskName, progress ->
                newProgress[taskName] = progress
            }
            dailyEntryRecyclerView.adapter = dailyAdapter
            dailyEntryRecyclerView.layoutManager = LinearLayoutManager(this)

            MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Add Daily Progress")
                .setPositiveButton("Save") { _, _ ->
                    val calendar = Calendar.getInstance().apply {
                        set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    }
                    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

                    val dailyLog = DailyLog(date = dateString, progress = newProgress)
                    
                    // FIX: Create new lists because Project uses immutable lists
                    val updatedLogs = currentProject.daily_logs.toMutableList()
                    updatedLogs.add(dailyLog)
                    
                    // Update task current values
                    val updatedTasks = currentProject.tasks.map { task ->
                        val addedProgress = newProgress[task.name] ?: 0.0
                        task.copy(current = task.current + addedProgress)
                    }
                    
                    // Create new project object with updated data
                    val updatedProject = currentProject.copy(
                        daily_logs = updatedLogs,
                        tasks = updatedTasks
                    )
                    
                    // Update local reference
                    project = updatedProject
                    
                    saveProjectAndUpdateUI(updatedProject)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun saveProjectAndUpdateUI(p: Project) {
        lifecycleScope.launch {
            try {
                // First save to local database
                db.projectDao().updateProject(p)
                
                // Then send to server using the update endpoint
                val updatedProject = com.example.dailyactivityregister.network.RetrofitInstance.api.updateProject(p.project_name, p)
                
                Toast.makeText(this@ProjectDashboardActivity, "Daily Log Saved and synced to server!", Toast.LENGTH_SHORT).show()

                // Update UI with the latest data from server
                val totals = calculateTotals(updatedProject)
                adapter.updateData(updatedProject.tasks, totals)
                
                // Update local reference
                project = updatedProject
            } catch (e: Exception) {
                // If server update fails, still show local save success but warn user
                Toast.makeText(this@ProjectDashboardActivity, "Saved locally, but failed to sync to server: ${e.message}", Toast.LENGTH_LONG).show()
                
                val totals = calculateTotals(p)
                adapter.updateData(p.tasks, totals)
            }
        }
    }

    private fun calculateTotals(p: Project): Map<String, Double> {
        val totals = mutableMapOf<String, Double>()
        p.tasks.forEach { task ->
            // Use the persisted current value directly from the server
            totals[task.name] = task.current
        }
        return totals
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
            parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
        } catch (e: Exception) {
             try {
                val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
            } catch (e2: Exception) {
                dateString
            }
        }
    }
}