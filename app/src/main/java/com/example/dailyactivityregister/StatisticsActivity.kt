package com.example.dailyactivityregister

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import kotlinx.coroutines.launch

class StatisticsActivity : ComponentActivity() {
    
    private val modelProducer = CartesianChartModelProducer()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                StatisticsScreen(
                    onBackClick = { finish() },
                    modelProducer = modelProducer
                )
            }
        }
        
        // Load data
        loadStatistics()
    }
    
    private fun loadStatistics() {
        lifecycleScope.launch {
            try {
                val projects = com.example.dailyactivityregister.network.RetrofitInstance.api.getProjects()
                
                // Calculate statistics
                val totalProjects = projects.size
                val subdivisionCounts = projects.groupBy { it.subdivision ?: "Unknown" }
                    .mapValues { it.value.size }
                
                // Calculate average completion
                val completions = projects.map { project ->
                    val totalTarget = project.tasks.sumOf { it.target }
                    val totalCurrent = project.tasks.sumOf { it.current }
                    if (totalTarget > 0) (totalCurrent / totalTarget * 100) else 0.0
                }
                
                // Update chart with subdivision data
                modelProducer.runTransaction {
                    columnSeries {
                        series(subdivisionCounts.values.toList())
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit,
    modelProducer: CartesianChartModelProducer
) {
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load projects
    LaunchedEffect(Unit) {
        try {
            projects = com.example.dailyactivityregister.network.RetrofitInstance.api.getProjects()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    
    // Calculate statistics
    val totalProjects = projects.size
    val subdivisionCounts = projects.groupBy { it.subdivision ?: "Unknown" }
        .mapValues { it.value.size }
    
    val avgCompletion = if (projects.isNotEmpty()) {
        projects.map { project ->
            val totalTarget = project.tasks.sumOf { it.target }
            val totalCurrent = project.tasks.sumOf { it.current }
            if (totalTarget > 0) (totalCurrent / totalTarget * 100) else 0.0
        }.average()
    } else 0.0
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Projects",
                        value = totalProjects.toString(),
                        color = Color(0xFF6200EE),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Completion",
                        value = "${avgCompletion.toInt()}%",
                        color = Color(0xFF03DAC5),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Subdivision Breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Projects by Subdivision",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        subdivisionCounts.forEach { (subdivision, count) ->
                            SubdivisionRow(
                                name = subdivision,
                                count = count,
                                total = totalProjects
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                // Chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Project Distribution",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberColumnCartesianLayer(),
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Task Progress Overview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Overall Task Progress",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        val taskTypes = listOf("Survey", "Excavation", "Erection", "Stringing")
                        taskTypes.forEach { taskType ->
                            val taskProgress = projects.flatMap { it.tasks }
                                .filter { it.name == taskType }
                                .let { tasks ->
                                    val totalTarget = tasks.sumOf { it.target }
                                    val totalCurrent = tasks.sumOf { it.current }
                                    if (totalTarget > 0) (totalCurrent / totalTarget * 100) else 0.0
                                }
                            
                            TaskProgressRow(
                                taskName = taskType,
                                progress = taskProgress.toFloat()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 14.sp,
                color = color.copy(alpha = 0.8f)
            )
            Text(
                value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun SubdivisionRow(name: String, count: Int, total: Int) {
    val percentage = if (total > 0) (count.toFloat() / total * 100) else 0f
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontWeight = FontWeight.Medium)
            Text("$count projects", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun TaskProgressRow(taskName: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(taskName, fontWeight = FontWeight.Medium)
            Text("${progress.toInt()}%", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when {
                progress >= 75 -> Color(0xFF4CAF50)
                progress >= 50 -> Color(0xFFFFC107)
                progress >= 25 -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
