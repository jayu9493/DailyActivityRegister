# 🚀 Deploy to Railway - Quick Guide

## ✅ Backend is Cloud-Ready!

Your `main.py` is now configured to use environment variables.

---

## 📋 Step 1: Get Supabase Connection String

1. Go to https://supabase.com/dashboard
2. Click your project: `daily-activity-register`
3. Go to **Settings** → **Database**
4. Scroll to **Connection string**
5. Select **URI** tab
6. Copy the connection string (looks like):
   ```
   postgresql://postgres.[PROJECT-REF]:[YOUR-PASSWORD]@aws-0-ap-south-1.pooler.supabase.com:6543/postgres
   ```
7. **IMPORTANT**: Replace `[YOUR-PASSWORD]` with your actual database password

---

## 📋 Step 2: Deploy to Railway

### 2.1 Sign Up
1. Go to https://railway.app
2. Click "Login" → "Login with GitHub"
3. Authorize Railway

### 2.2 Create New Project
1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. If not connected, click "Configure GitHub App"
4. Select your repository
5. Select branch: `feature/cloud-migration`
6. Railway will detect Python automatically

### 2.3 Configure Root Directory
1. In Railway dashboard, click your service
2. Go to "Settings" tab
3. Scroll to "Root Directory"
4. Set to: `backend/DailyActivityRegisterBackend`
5. Click "Update"

### 2.4 Add Environment Variable
1. Go to "Variables" tab
2. Click "New Variable"
3. Add:
   - **Key**: `DATABASE_URL`
   - **Value**: (paste your Supabase connection string from Step 1)
4. Click "Add"

### 2.5 Deploy!
1. Go to "Deployments" tab
2. Click "Deploy"
3. Wait 2-3 minutes
4. You'll get a URL like: `https://your-app.up.railway.app`

---

## 📋 Step 3: Update Android App

1. Open `RetrofitInstance.kt`
2. Change line 10:
   ```kotlin
   private const val BASE_URL = "https://your-app.up.railway.app/"
   ```
   (Replace with your Railway URL)
3. Rebuild app
4. Test!

---

## ✅ Verification

Test these in your Android app:
- [ ] View projects
- [ ] Upload Excel file
- [ ] Update project
- [ ] Subdivision filter
- [ ] Statistics dashboard

---

## 🎉 Done!

Your app is now **fully cloud-native**! Works from anywhere! 🌍☁️

---

## 🆘 Troubleshooting

**"Application failed to respond":**
- Check Railway logs
- Verify DATABASE_URL is correct
- Make sure root directory is set

**"Database connection failed":**
- Double-check Supabase connection string
- Verify password is correct
- Check if Supabase project is active

**"Module not found":**
- Verify `requirements.txt` is in root directory
- Check Railway build logs

---

**Ready? Start with Step 1!** 🚀
