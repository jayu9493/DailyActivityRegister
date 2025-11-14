package com.example.dailyactivityregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyactivityregister.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getSerializableExtra("PROJECT") as Project
        title = "Add Activity for ${project.name}"

        binding.projectName.text = project.name

        binding.saveButton.setOnClickListener {
            val activityEntry = ActivityEntry(
                projectName = project.name,
                date = binding.activityDate.text.toString(),
                progressDetails = binding.progressDetails.text.toString(),
                manpower = binding.manpower.text.toString(),
                bottlenecks = binding.bottlenecks.text.toString(),
                planForNextDay = binding.planForNextDay.text.toString()
            )

            project.activities.add(activityEntry)

            val resultIntent = Intent()
            resultIntent.putExtra("UPDATED_PROJECT", project)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
