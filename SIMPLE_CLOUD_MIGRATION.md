# ☁️ Cloud Migration - Super Simple Guide

## 🎯 What This Does
Automatically copies all your projects from local database to Supabase cloud in **3 easy steps**!

---

## 📝 Step 1: Create Supabase Account (2 minutes)

1. Go to **https://supabase.com**
2. Click **"Start your project"**
3. Sign up with **GitHub** (easiest) or email
4. Verify your email

---

## 🚀 Step 2: Create Supabase Project (3 minutes)

1. Click **"New Project"**
2. Fill in:
   - **Name**: `daily-activity-register`
   - **Database Password**: Click "Generate a password" and **COPY IT!**
   - **Region**: `ap-south-1` (Mumbai, India) or closest to you
   - **Plan**: Free
3. Click **"Create new project"**
4. ⏰ **Wait 2-3 minutes** for setup (grab a coffee!)

---

## 🔑 Step 3: Get Your Credentials (1 minute)

Once project is ready:

1. Go to **Settings** (⚙️ icon on left sidebar)
2. Click **API**
3. You'll see:
   - **Project URL**: `https://xxx.supabase.co`
   - **API Keys** section with **anon/public** key

4. **Copy these 2 things:**
   - Project URL
   - anon public key (the long one starting with `eyJ...`)

---

## ⚙️ Step 4: Configure Migration Script (30 seconds)

1. Open file: `backend\DailyActivityRegisterBackend\scripts\supabase_config.py`
2. Replace:
   ```python
   SUPABASE_URL = "https://your-project.supabase.co"  # Paste your URL here
   SUPABASE_KEY = "your-anon-key-here"  # Paste your anon key here
   ```
3. Save the file

---

## 🎬 Step 5: Run Migration (1 minute)

Open terminal in `backend\DailyActivityRegisterBackend\scripts\` and run:

```bash
python migrate_to_supabase.py
```

The script will:
- ✅ Connect to your local database
- ✅ Read all projects
- ✅ Upload to Supabase
- ✅ Show progress for each project

**That's it!** Your data is now in the cloud! ☁️

---

## ✅ Verify Migration

1. Go to Supabase Dashboard
2. Click **Table Editor** (📊 icon)
3. Click **projects** table
4. You should see all your projects!

---

## 📱 Next: Update Android App

After migration succeeds, we'll update the Android app to use Supabase (next session).

For now, your app will still use the local backend - both will work!

---

## 🆘 Troubleshooting

**"Supabase not configured" error:**
- Make sure you pasted the URL and key in `supabase_config.py`
- URL should start with `https://`
- Key should be the long `eyJ...` string

**"Connection error":**
- Check your internet connection
- Verify the URL is correct
- Make sure Supabase project is fully created (wait 3 minutes)

**"401 Unauthorized":**
- Double-check you copied the **anon/public** key (not the service_role key)

---

## 🎯 Total Time: ~10 minutes

- Create account: 2 min
- Create project: 3 min  
- Get credentials: 1 min
- Configure: 30 sec
- Run migration: 1 min
- Verify: 30 sec

**Ready? Start with Step 1!** 🚀
