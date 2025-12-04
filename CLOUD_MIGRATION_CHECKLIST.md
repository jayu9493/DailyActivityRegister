# Cloud Migration - Quick Start Checklist

## ✅ Preparation (DONE)
- [x] Created Git branch: `feature/cloud-migration`
- [x] Created migration guide
- [x] Created backup script

---

## 🚀 Your Next Steps

### Step 1: Backup Database (5 minutes)
```bash
.\backup_database.bat
```
This will create 3 backup files in `database_backups/` folder.

### Step 2: Create Supabase Account (5 minutes)
1. Go to https://supabase.com
2. Click "Start your project"
3. Sign up (use GitHub for easier login)
4. Verify email

### Step 3: Create Supabase Project (3 minutes)
1. Click "New Project"
2. Fill in:
   - Name: `daily-activity-register`
   - Database Password: **Generate strong password and SAVE IT!**
   - Region: `ap-south-1` (India) or closest to you
3. Click "Create new project"
4. Wait 2-3 minutes

### Step 4: Get Your Credentials (2 minutes)
Once project is ready:
1. Go to **Settings** → **API**
2. Copy and save:
   - **Project URL**: `https://xxx.supabase.co`
   - **anon/public key**: `eyJhbG...` (long string)
3. Go to **Settings** → **Database**
4. Copy **Connection string** (we'll use this for migration)

### Step 5: Import Database (10 minutes)
**Option A: Using Supabase Dashboard (Easier)**
1. Go to **SQL Editor** in Supabase
2. Click "New query"
3. Open `database_backups/schema_[timestamp].sql` in notepad
4. Copy and paste into SQL Editor
5. Click "Run"
6. Repeat for `data_[timestamp].sql`

**Option B: Using Command Line**
```bash
# Replace [YOUR-PASSWORD] and [YOUR-PROJECT-URL]
psql "postgresql://postgres:[YOUR-PASSWORD]@db.[YOUR-PROJECT-URL].supabase.co:5432/postgres" < database_backups/schema_[timestamp].sql
psql "postgresql://postgres:[YOUR-PASSWORD]@db.[YOUR-PROJECT-URL].supabase.co:5432/postgres" < database_backups/data_[timestamp].sql
```

### Step 6: Verify Data (2 minutes)
In Supabase Dashboard:
1. Go to **Table Editor**
2. Click on `projects` table
3. Verify your data is there!

---

## 📱 Android App Integration (Next Session)

Once database is migrated, we'll:
1. Add Supabase SDK to Android app
2. Create Supabase client
3. Update API calls
4. Test everything

---

## 🆘 If Something Goes Wrong

**Rollback to local setup:**
```bash
git checkout main
```

**Restore database from backup:**
```bash
psql -h localhost -U postgres -d daily_activity_db < database_backups/full_backup_[timestamp].sql
```

---

## 📞 Need Help?

Check the detailed guide: `CLOUD_MIGRATION_GUIDE.md`

---

**Ready? Start with Step 1: Run `.\backup_database.bat`** 🚀
