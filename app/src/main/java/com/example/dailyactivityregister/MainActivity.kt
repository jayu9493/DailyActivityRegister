package com.example.dailyactivityregister

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyactivityregister.databinding.ActivityMainBinding
import com.example.dailyactivityregister.network.ProjectCreateRequest
import com.example.dailyactivityregister.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before calling super.onCreate()
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        fetchProjects()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the project list when returning to this screen
        // This ensures deleted projects are removed from the list
        fetchProjects()
    }

    private fun setupRecyclerView() {
        projects = mutableListOf()
        adapter = ProjectAdapter(projects) { project ->
            val intent = Intent(this, ProjectDashboardActivity::class.java)
            intent.putExtra("PROJECT", project)
            startActivity(intent)
        }
        binding.contentMain.recyclerView.adapter = adapter
        binding.contentMain.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchProjects() {
        lifecycleScope.launch {
            try {
                val projectList = RetrofitInstance.api.getProjects()
                projects.clear()
                projects.addAll(projectList)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to fetch projects: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val excelFilePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val fileBytes = inputStream?.readBytes() ?: return@launch
                    inputStream.close()

                    val requestBody = fileBytes.toRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", "project.xlsx", requestBody)

                    RetrofitInstance.api.uploadProjectFile(part)
                    Toast.makeText(this@MainActivity, "File uploaded successfully! Refreshing...", Toast.LENGTH_SHORT).show()
                    fetchProjects()
                } catch (e: Exception) {
                    val errorMsg = if (e is HttpException && e.code() == 400) "Invalid Excel format." else e.message
                    Toast.makeText(this@MainActivity, "Upload failed: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val addProjectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val projectData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("NEW_PROJECT_DATA", ProjectCreateRequest::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("NEW_PROJECT_DATA") as? ProjectCreateRequest
            }

            projectData?.let { data ->
                lifecycleScope.launch {
                    try {
                        RetrofitInstance.api.createProject(data)
                        Toast.makeText(this@MainActivity, "Project created successfully!", Toast.LENGTH_SHORT).show()
                        fetchProjects()
                    } catch (e: Exception) {
                        val errorMsg = if (e is HttpException) {
                            when (e.code()) {
                                400 -> "Project with this name already exists"
                                else -> e.message()
                            }
                        } else e.message
                        Toast.makeText(this@MainActivity, "Failed to create project: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upload_excel -> {
                excelFilePickerLauncher.launch("*/*")
                true
            }
            R.id.action_add_project -> {
                val intent = Intent(this, AddProjectActivity::class.java)
                addProjectLauncher.launch(intent)
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}