package com.example.dailyactivityregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dailyactivityregister.network.ProjectCreateRequest
import com.example.dailyactivityregister.ui.theme.DailyActivityRegisterTheme
import java.io.Serializable

class AddProjectActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyActivityRegisterTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Add New Project") })
                    }
                ) {
                    // The onSave callback now returns the simple request object
                    AddProjectScreen(Modifier.padding(it)) { request ->
                        val resultIntent = Intent()
                        resultIntent.putExtra("NEW_PROJECT_DATA", request as Serializable)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }
            }
        }
    }
}

// The Composable no longer knows about the full Project object.
// It only knows about the data it needs to collect.
@Composable
fun AddProjectScreen(modifier: Modifier = Modifier, onSave: (ProjectCreateRequest) -> Unit) {
    var projectName by remember { mutableStateOf("") }
    var projectNumber by remember { mutableStateOf("") }
    var villages by remember { mutableStateOf("") }
    var totalRouteOh by remember { mutableStateOf("") }
    var totalRouteUg by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(value = projectName, onValueChange = { projectName = it }, label = { Text("Project Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = projectNumber, onValueChange = { projectNumber = it }, label = { Text("Project Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = totalRouteOh, onValueChange = { totalRouteOh = it }, label = { Text("Total Route OH (km)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = totalRouteUg, onValueChange = { totalRouteUg = it }, label = { Text("Total Route UG (km)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = villages, onValueChange = { villages = it }, label = { Text("Line Passing Villages") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val request = ProjectCreateRequest(
                    project_name = projectName,
                    project_number = projectNumber,
                    total_route_oh = totalRouteOh.toDoubleOrNull() ?: 0.0,
                    total_route_ug = totalRouteUg.toDoubleOrNull() ?: 0.0,
                    line_passing_villages = villages
                )
                onSave(request)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
