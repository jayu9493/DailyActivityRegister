# Daily Activity Register

Android application for tracking daily activities and progress of electrical transmission line projects.

## Features

- 📊 **Statistics Dashboard** - Visual charts and progress tracking
- 🔍 **Subdivision Filter** - Filter projects by location
- 📤 **Excel Upload** - Parse and import project data from Excel files
- ☁️ **Cloud Sync** - Backend deployed on Render with Supabase database
- 📱 **Modern UI** - Material 3 design with Jetpack Compose

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material 3
- **Backend**: Python, FastAPI, PostgreSQL
- **Database**: Supabase (Cloud PostgreSQL)
- **Deployment**: Render
- **Charts**: Vico

## Setup

### Android App
1. Open project in Android Studio
2. Sync Gradle
3. Build and run

### Backend (Local Development)
1. Navigate to `backend/DailyActivityRegisterBackend`
2. Install dependencies: `pip install -r requirements.txt`
3. Run server: `uvicorn scripts.main:app --reload`

## Cloud Deployment

Backend is deployed at: `https://dailyactivityregister.onrender.com`

Database: Supabase PostgreSQL

## Documentation

All guides and documentation are in the `docs/` folder (local only, not tracked in Git).
