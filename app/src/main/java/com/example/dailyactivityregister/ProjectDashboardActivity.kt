package com.example.dailyactivityregister

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyactivityregister.databinding.ActivityProjectDashboardBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDashboardBinding
    private var project: Project? = null
    private lateinit var db: AppDatabase
    private lateinit var adapter: ProjectTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        project = intent.getSerializableExtra("PROJECT") as? Project

        project?.let {
            setupDashboard(it)
        } ?: run {
            Toast.makeText(this, "Error: Project data is missing.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupDashboard(p: Project) {
        title = p.project_name

        val totalRoute = p.oh_line_length + p.ug_line_length
        binding.commencementDate.text = "Commencement: ${formatDate(p.commencement_date)}"
        binding.totalRoute.text = String.format(Locale.US, "Total Route: %.3f km (OH: %.3f, UG: %.3f)", totalRoute, p.oh_line_length, p.ug_line_length)
        binding.villages.text = "Villages: ${p.line_passing_villages}"

        val totals = calculateTotals(p)
        adapter = ProjectTaskAdapter(p.tasks, totals)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addDailyEntryButton.setOnClickListener {
            showAddDailyEntryDialog()
        }
    }

    private fun showAddDailyEntryDialog() {
        // This function will be implemented in the next step
        Toast.makeText(this, "Add Daily Entry Clicked!", Toast.LENGTH_SHORT).show()
    }

    private fun calculateTotals(p: Project): Map<String, Double> {
        val totals = mutableMapOf<String, Double>()
        p.tasks.forEach { task ->
            totals[task.name] = task.current
        }
        return totals
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            if (dateString.contains("T")) {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
            } else {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }
}