# Daily Activity Register

This repository contains an Android application designed to help manage and track daily activities for construction projects. It provides a simple, local-first solution for recording project progress, manpower, and other critical details.

## Core Features

1.  **Project Management**: Users can create, view, and manage a list of construction projects.
2.  **Structured Task Tracking**: For each project, users can update the progress of specific, measurable tasks (e.g., "Excavation," "Stringing"), paving the way for detailed dashboards.
3.  **Data Persistence**: All project and task data is stored locally on the device in a Room database, ensuring that information is saved permanently.
4.  **Modern UI**: The app is built with Material 3 and includes modern UI components like Jetpack Compose for a clean and intuitive user experience.

## Code and Architecture Explained

The application is built using modern Android development practices, including Kotlin, View Binding for the main screen, Jetpack Compose for newer screens, and the Room persistence library for local storage.

### 1. Build Configuration (`build.gradle.kts`)

The app's build process is managed by Gradle. Key dependencies and features are enabled here.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Enables the Kotlin Annotation Processing Tool for Room
}

android {
    // ... other configurations
    buildFeatures {
        viewBinding = true // Enables View Binding for the legacy XML layouts
        compose = true     // Enables Jetpack Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Room Database for local persistence
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Provides Kotlin coroutine support
    kapt(libs.androidx.room.compiler) // The annotation processor for Room

    // Jetpack Compose for modern UI development
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    
    // Gson for JSON serialization (used by the database type converter)
    implementation(libs.gson)
}
```

### 2. Data Models (`Project.kt` and `ProjectTask.kt`)

These data classes define the structure of the application's data.

**`Project.kt`**:
This is the main entity for our database. It holds the project's name, agency, and a list of its structured tasks.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/Project.kt

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val name: String,
    val agency: String,
    @TypeConverters(ProjectTaskConverter::class) // Tells Room how to store the list
    val tasks: MutableList<ProjectTask> = mutableListOf()
) : Serializable
```

**`ProjectTask.kt`**:
This class represents a single, measurable task within a project.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/ProjectTask.kt

import java.io.Serializable

data class ProjectTask(
    val name: String,
    val target: Double,
    var current: Double = 0.0,
    val unit: String // e.g., "km", "Nos.", "%"
) : Serializable
```

### 3. Database Layer (Room)

This layer handles all database operations, providing a robust and efficient way to store and retrieve data locally.

**`AppDatabase.kt`**:
This class represents the database itself. It's a singleton to ensure only one instance is ever created. The version is now `2` to reflect the change in our data model.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/AppDatabase.kt

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Project::class], version = 2, exportSchema = false)
@TypeConverters(ProjectTaskConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        // Singleton pattern
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration() // Clears the DB on version change
                    .build()
            }
        }
    }
}
```

**`ProjectTaskConverter.kt`**:
Since Room can't store a list of custom objects directly, this converter uses the `Gson` library to convert the `MutableList<ProjectTask>` into a JSON string for storage, and back again upon retrieval.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/ProjectTaskConverter.kt

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProjectTaskConverter {
    @TypeConverter
    fun fromString(value: String): MutableList<ProjectTask> {
        val listType = object : TypeToken<MutableList<ProjectTask>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<ProjectTask>): String {
        return Gson().toJson(list)
    }
}
```

### 4. UI and Application Logic

**`MainActivity.kt`**:
This is the main screen, displaying the list of all projects. It loads project data from the database and launches the dashboard when a project is clicked.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/MainActivity.kt

class MainActivity : AppCompatActivity() {
    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        // ...
        db = AppDatabase.getDatabase(this)

        // Load projects from the database using a coroutine
        lifecycleScope.launch {
            projects = db.projectDao().getAllProjects().toMutableList()

            adapter = ProjectAdapter(projects) { project ->
                // When a project is clicked, open its dashboard
                val intent = Intent(this@MainActivity, ProjectDashboardActivity::class.java)
                intent.putExtra("PROJECT", project)
                startActivity(intent)
            }
            binding.contentMain.recyclerView.adapter = adapter
        }
    }
    // ...
}
```

**`ProjectDashboardActivity.kt`**:
This screen serves as the main dashboard for a single project. It displays the list of tasks and allows the user to update the progress for each one. Progress is saved automatically when the user leaves the screen.

```kotlin
// C:/Users/jayrp/AndroidStudioProjects/DailyActivityRegister/app/src/main/java/com/example/dailyactivityregister/ProjectDashboardActivity.kt

class ProjectDashboardActivity : AppCompatActivity() {
    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        // ...
        project = intent.getSerializableExtra("PROJECT") as Project

        val adapter = ProjectTaskAdapter(project.tasks) { task, progress ->
            task.current = progress // Update the task's progress in memory
        }
        binding.recyclerView.adapter = adapter
    }

    // When the user leaves the screen, save the updated project to the database
    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            db.projectDao().updateProject(project)
        }
    }
}
```

### 5. Future Enhancements: Excel Import and Dashboards

(This section remains the same as it describes the future plan.)

```
