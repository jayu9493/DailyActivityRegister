# Full Cloud Migration - Deployment Guide

## 🎯 Strategy: Deploy FastAPI Backend to Cloud

We'll deploy your entire FastAPI backend (with Excel parsing) to **Railway** (free tier), connected to Supabase PostgreSQL.

---

## 📋 Step 1: Prepare Backend for Deployment

### 1.1 Create requirements.txt

In `backend/DailyActivityRegisterBackend/`, create `requirements.txt`:

```
fastapi==0.104.1
uvicorn[standard]==0.24.0
sqlalchemy==2.0.23
psycopg2-binary==2.9.9
pydantic==2.5.0
python-multipart==0.0.6
openpyxl==3.1.2
pandas==2.1.3
python-dotenv==1.0.0
```

### 1.2 Create Procfile

In `backend/DailyActivityRegisterBackend/`, create `Procfile`:

```
web: cd scripts && uvicorn main:app --host 0.0.0.0 --port $PORT
```

### 1.3 Update main.py for cloud

Add environment variable support for database URL.

---

## 📋 Step 2: Deploy to Railway

### 2.1 Create Railway Account
1. Go to https://railway.app
2. Sign up with GitHub (easiest)
3. Verify email

### 2.2 Create New Project
1. Click "New Project"
2. Select "Deploy from GitHub repo"
3. Connect your GitHub account
4. Select your repository
5. Select the `feature/cloud-migration` branch

### 2.3 Configure Environment Variables
In Railway dashboard:
1. Go to "Variables" tab
2. Add these variables:
   ```
   DATABASE_URL=postgresql://postgres:[PASSWORD]@db.[PROJECT].supabase.co:5432/postgres
   ```
   (Use your Supabase connection string)

### 2.4 Deploy!
- Railway will automatically detect Python
- Build and deploy your backend
- You'll get a URL like: `https://your-app.railway.app`

---

## 📋 Step 3: Update Android App

### 3.1 Update RetrofitInstance.kt

Change BASE_URL to your Railway URL:
```kotlin
private const val BASE_URL = "https://your-app.railway.app/"
```

### 3.2 Rebuild and Test
- Rebuild Android app
- Test all features
- Everything should work from anywhere!

---

## 🎁 What You'll Have

✅ **FastAPI backend** running in the cloud  
✅ **PostgreSQL database** in Supabase  
✅ **Android app** works from anywhere  
✅ **Excel parsing** works in the cloud  
✅ **No local server needed**  

---

## 💰 Cost

**Railway Free Tier:**
- $5 credit/month
- ~500 hours runtime
- Perfect for your use case
- **Cost: $0/month** (within free tier)

**Supabase Free Tier:**
- 500MB database
- Unlimited API requests
- **Cost: $0/month**

**Total: $0/month** 🎉

---

## ⏱️ Time Estimate

- Prepare files: 10 minutes
- Deploy to Railway: 5 minutes
- Update Android app: 2 minutes
- Test: 5 minutes

**Total: ~25 minutes**

---

**Ready to start?** I'll create all the necessary files for you!
