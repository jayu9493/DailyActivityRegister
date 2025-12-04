# Cloud Migration Guide - Supabase Integration

## 🎯 Overview
This guide will help you migrate your Daily Activity Register app from local PostgreSQL to **Supabase** (cloud PostgreSQL).

---

## ✅ Phase 1: Preparation (COMPLETED)
- [x] Created Git branch: `feature/cloud-migration`
- [x] Working tree is clean
- [x] Ready for migration

---

## 📝 Phase 2: Supabase Account Setup

### Step 1: Create Supabase Account
1. Go to [https://supabase.com](https://supabase.com)
2. Click "Start your project"
3. Sign up with GitHub (recommended) or email
4. **Free tier includes:**
   - 500MB database storage
   - Unlimited API requests
   - 2GB file storage
   - 50MB file uploads

### Step 2: Create New Project
1. Click "New Project"
2. Fill in details:
   - **Organization**: Create new or use existing
   - **Project Name**: `daily-activity-register`
   - **Database Password**: Generate a strong password (SAVE THIS!)
   - **Region**: Choose closest to you (e.g., `ap-south-1` for India)
   - **Pricing Plan**: Free
3. Click "Create new project"
4. Wait 2-3 minutes for setup

### Step 3: Get Connection Details
Once project is ready:
1. Go to **Settings** → **Database**
2. Copy these details:
   - **Host**: `db.xxx.supabase.co`
   - **Database name**: `postgres`
   - **Port**: `5432`
   - **User**: `postgres`
   - **Password**: (the one you set)
3. Also copy the **Connection String** (we'll use this)

---

## 🗄️ Phase 3: Database Migration

### Step 1: Export Current Schema
Run this command to export your current database schema:

```bash
pg_dump -h localhost -U postgres -d daily_activity_db --schema-only > schema_backup.sql
```

### Step 2: Export Current Data
```bash
pg_dump -h localhost -U postgres -d daily_activity_db --data-only > data_backup.sql
```

### Step 3: Import to Supabase
1. Go to Supabase Dashboard → **SQL Editor**
2. Create a new query
3. Paste the schema from `schema_backup.sql`
4. Click "Run"
5. Then paste data from `data_backup.sql`
6. Click "Run"

**OR** use command line:
```bash
psql "postgresql://postgres:[YOUR-PASSWORD]@db.xxx.supabase.co:5432/postgres" < schema_backup.sql
psql "postgresql://postgres:[YOUR-PASSWORD]@db.xxx.supabase.co:5432/postgres" < data_backup.sql
```

---

## 📱 Phase 4: Android App Integration

### Step 1: Add Supabase SDK
We'll add Supabase Kotlin SDK to your Android app.

**In `gradle/libs.versions.toml`**, add:
```toml
[versions]
supabase = "2.0.0"
ktor = "2.3.7"

[libraries]
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt", version.ref = "supabase" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
```

**In `app/build.gradle.kts`**, add:
```kotlin
implementation(libs.supabase.postgrest)
implementation(libs.supabase.realtime)
implementation(libs.ktor.client.android)
```

### Step 2: Create Supabase Client
Create new file: `SupabaseClient.kt`

```kotlin
package com.example.dailyactivityregister.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://xxx.supabase.co",  // Your project URL
        supabaseKey = "your-anon-key"  // Your anon/public key
    ) {
        install(Postgrest)
        install(Realtime)
    }
}
```

### Step 3: Update API Calls
Replace Retrofit calls with Supabase calls. Example:

**Before (Retrofit):**
```kotlin
val projects = RetrofitInstance.api.getProjects()
```

**After (Supabase):**
```kotlin
val projects = SupabaseClient.client.from("projects")
    .select()
    .decodeList<Project>()
```

---

## 🔒 Phase 5: Security Setup

### Enable Row Level Security (RLS)
In Supabase SQL Editor:

```sql
-- Enable RLS on projects table
ALTER TABLE projects ENABLE ROW LEVEL SECURITY;

-- Allow public read access (for now)
CREATE POLICY "Allow public read" ON projects
FOR SELECT USING (true);

-- Allow public insert/update (you can restrict this later)
CREATE POLICY "Allow public insert" ON projects
FOR INSERT WITH CHECK (true);

CREATE POLICY "Allow public update" ON projects
FOR UPDATE USING (true);
```

---

## ✅ Phase 6: Testing

### Test Checklist:
- [ ] Can fetch projects from Supabase
- [ ] Can create new project
- [ ] Can update project
- [ ] Can delete project
- [ ] Subdivision filter works
- [ ] Statistics dashboard works
- [ ] Pull-to-refresh works

---

## 🚀 Phase 7: Deployment

### Option A: Keep Both (Recommended for now)
- Keep local backend running
- Add a settings toggle to switch between local/cloud
- Test thoroughly before full migration

### Option B: Full Migration
- Update all API calls to use Supabase
- Remove local backend dependency
- Deploy to production

---

## 🆘 Rollback Plan

If anything goes wrong:
```bash
git checkout main
git branch -D feature/cloud-migration
```

Your local setup will be completely restored!

---

## 📊 Cost Estimation

**Supabase Free Tier:**
- 500MB database (plenty for your use case)
- Unlimited API requests
- 2GB bandwidth/month
- **Cost: $0/month**

**When you outgrow free tier:**
- Pro plan: $25/month
- Includes 8GB database, 50GB bandwidth

---

## 🎯 Next Steps

1. **Create Supabase account** (5 minutes)
2. **Create project** (3 minutes)
3. **Export & import database** (10 minutes)
4. **Test connection** (5 minutes)
5. **Update Android app** (30 minutes)
6. **Test thoroughly** (30 minutes)

**Total time: ~1.5 hours**

---

## 📞 Support

If you get stuck:
- Supabase Docs: https://supabase.com/docs
- Supabase Discord: https://discord.supabase.com
- Or ask me for help!

---

**Ready to start? Let's begin with creating your Supabase account!** 🚀
