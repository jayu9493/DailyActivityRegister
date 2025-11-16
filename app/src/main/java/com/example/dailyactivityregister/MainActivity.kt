package com.example.dailyactivityregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailyactivityregister.databinding.ActivityMainBinding
import com.example.dailyactivityregister.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        db = AppDatabase.getDatabase(this)

        setupRecyclerView()
        fetchProjects()
    }

    private fun setupRecyclerView() {
        projects = mutableListOf()
        adapter = ProjectAdapter(projects) { project ->
            val intent = Intent(this@MainActivity, ProjectDashboardActivity::class.java)
            intent.putExtra("PROJECT", project)
            startActivity(intent)
        }
        binding.contentMain.recyclerView.adapter = adapter
        binding.contentMain.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun fetchProjects() {
        lifecycleScope.launch {
            try {
                val projectList = RetrofitInstance.api.getProjects()
                projects.clear()
                projects.addAll(projectList)
                adapter.notifyDataSetChanged()

                // Optionally, save the fetched projects to the local DB for caching
                db.projectDao().insertAll(projectList)
            } catch (e: Exception) {
                // If the network call fails, try to load from the local database
                val cachedProjects = db.projectDao().getAllProjects()
                projects.clear()
                projects.addAll(cachedProjects)
                adapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Couldn\'t fetch new data. Showing offline projects.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val addProjectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchProjects() // Refresh the project list from the server
        }
    }

    private val excelFilePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val fileBytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (fileBytes != null) {
                        val requestBody = fileBytes.toRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("file", "project.xlsx", requestBody)

                        val newProject = RetrofitInstance.api.uploadProjectFile(part)
                        Toast.makeText(this@MainActivity, "Successfully uploaded: ${newProject.project_name}", Toast.LENGTH_LONG).show()
                        fetchProjects() // Refresh the list
                    } else {
                        Toast.makeText(this@MainActivity, "Error reading file", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
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
            R.id.action_add_project -> {
                val intent = Intent(this, AddProjectActivity::class.java)
                addProjectLauncher.launch(intent)
                true
            }
            R.id.action_upload_excel -> {
                excelFilePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}