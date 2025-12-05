package com.example.dailyactivityregister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyactivityregister.network.AgencyRequest
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
                        TopAppBar(
                            title = { Text("Add New Project") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, "Back")
                                }
                            }
                        )
                    }
                ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(modifier: Modifier = Modifier, onSave: (ProjectCreateRequest) -> Unit) {
    var projectName by remember { mutableStateOf("") }
    var projectNumber by remember { mutableStateOf("") }
    var villages by remember { mutableStateOf("") }
    var totalRouteOh by remember { mutableStateOf("") }
    var totalRouteUg by remember { mutableStateOf("") }
    var towerCount by remember { mutableStateOf("") }
    
    // Type of Work selection
    var selectedWorkType by remember { mutableStateOf("") }
    var workTypeExpanded by remember { mutableStateOf(false) }
    val workTypes = listOf("UG Only", "OH Only", "UG + OH Both")
    
    // Subdivision selection
    var selectedSubdivision by remember { mutableStateOf("") }
    var subdivisionExpanded by remember { mutableStateOf(false) }
    val subdivisions = listOf("Mundra", "Bhuj", "Anjar-1", "Anjar-2", "Samakhayali")
    
    // Agencies list
    var agencies by remember { mutableStateOf(listOf<AgencyData>()) }
    
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Project Name
        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Name *") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Project Number (Number keypad)
        OutlinedTextField(
            value = projectNumber,
            onValueChange = { projectNumber = it },
            label = { Text("Project Number *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subdivision Dropdown
        ExposedDropdownMenuBox(
            expanded = subdivisionExpanded,
            onExpandedChange = { subdivisionExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedSubdivision,
                onValueChange = {},
                readOnly = true,
                label = { Text("Subdivision *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subdivisionExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = subdivisionExpanded,
                onDismissRequest = { subdivisionExpanded = false }
            ) {
                subdivisions.forEach { subdivision ->
                    DropdownMenuItem(
                        text = { Text(subdivision) },
                        onClick = {
                            selectedSubdivision = subdivision
                            subdivisionExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Type of Work Dropdown
        Text(
            "Type of Work",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = workTypeExpanded,
            onExpandedChange = { workTypeExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedWorkType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Work Type *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = workTypeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = workTypeExpanded,
                onDismissRequest = { workTypeExpanded = false }
            ) {
                workTypes.forEach { workType ->
                    DropdownMenuItem(
                        text = { Text(workType) },
                        onClick = {
                            selectedWorkType = workType
                            workTypeExpanded = false
                            // Reset route lengths when work type changes
                            when (workType) {
                                "UG Only" -> totalRouteOh = "0"
                                "OH Only" -> totalRouteUg = "0"
                            }
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Route Lengths Section
        Text(
            "Route Lengths",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Show OH fields only if OH work is selected
        if (selectedWorkType == "OH Only" || selectedWorkType == "UG + OH Both") {
            OutlinedTextField(
                value = totalRouteOh,
                onValueChange = { totalRouteOh = it },
                label = { Text("OH Line Length (km) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Overhead line - Foundation, Erection, Stringing") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tower Count
            OutlinedTextField(
                value = towerCount,
                onValueChange = { towerCount = it },
                label = { Text("Number of Towers *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("For Foundation and Erection tasks") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Show UG fields only if UG work is selected
        if (selectedWorkType == "UG Only" || selectedWorkType == "UG + OH Both") {
            OutlinedTextField(
                value = totalRouteUg,
                onValueChange = { totalRouteUg = it },
                label = { Text("UG Cable Length (km) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Underground cable - Excavation") }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        OutlinedTextField(
            value = villages,
            onValueChange = { villages = it },
            label = { Text("Line Passing Villages") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Agencies Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Agencies",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = {
                agencies = agencies + AgencyData()
            }) {
                Icon(Icons.Default.Add, "Add Agency")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Agency Cards
        agencies.forEachIndexed { index, agency ->
            AgencyCard(
                agency = agency,
                onUpdate = { updated ->
                    agencies = agencies.toMutableList().apply {
                        set(index, updated)
                    }
                },
                onDelete = {
                    agencies = agencies.toMutableList().apply {
                        removeAt(index)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Save Button
        val isFormValid = projectName.isNotEmpty() && 
                         projectNumber.isNotEmpty() && 
                         selectedSubdivision.isNotEmpty() &&
                         selectedWorkType.isNotEmpty() &&
                         when (selectedWorkType) {
                             "UG Only" -> totalRouteUg.isNotEmpty()
                             "OH Only" -> totalRouteOh.isNotEmpty() && towerCount.isNotEmpty()
                             "UG + OH Both" -> totalRouteOh.isNotEmpty() && totalRouteUg.isNotEmpty() && towerCount.isNotEmpty()
                             else -> false
                         }
        
        Button(
            onClick = {
                val ohLength = if (selectedWorkType == "UG Only") 0.0 else totalRouteOh.toDoubleOrNull() ?: 0.0
                val ugLength = if (selectedWorkType == "OH Only") 0.0 else totalRouteUg.toDoubleOrNull() ?: 0.0
                val towers = if (selectedWorkType == "UG Only") 0 else towerCount.toIntOrNull() ?: 0
                
                val request = ProjectCreateRequest(
                    project_name = projectName,
                    project_number = projectNumber.ifEmpty { null },
                    total_route_oh = ohLength,
                    total_route_ug = ugLength,
                    tower_count = towers,
                    line_passing_villages = villages.ifEmpty { null },
                    subdivision = selectedSubdivision.ifEmpty { null },
                    agencies = agencies.map { it.toRequest() }
                )
                onSave(request)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Create Project")
        }
    }
}

@Composable
fun AgencyCard(
    agency: AgencyData,
    onUpdate: (AgencyData) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Agency Details", fontWeight = FontWeight.Medium)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = agency.name,
                onValueChange = { onUpdate(agency.copy(name = it)) },
                label = { Text("Agency Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = agency.typeOfWork,
                onValueChange = { onUpdate(agency.copy(typeOfWork = it)) },
                label = { Text("Type of Work") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = agency.locationWise,
                onValueChange = { onUpdate(agency.copy(locationWise = it)) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = agency.poNumber,
                onValueChange = { onUpdate(agency.copy(poNumber = it)) },
                label = { Text("PO Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = agency.suborderNo,
                onValueChange = { onUpdate(agency.copy(suborderNo = it)) },
                label = { Text("Suborder Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Data class for managing agency state
data class AgencyData(
    val name: String = "",
    val typeOfWork: String = "",
    val locationWise: String = "",
    val poNumber: String = "",
    val suborderNo: String = ""
) {
    fun toRequest() = AgencyRequest(
        name = name,
        type_of_work = typeOfWork.ifEmpty { null },
        location_wise = locationWise.ifEmpty { null },
        po_number = poNumber.ifEmpty { null },
        suborder_no = suborderNo.ifEmpty { null }
    )
}
