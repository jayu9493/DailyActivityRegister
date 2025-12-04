# Excel Upload Troubleshooting Guide

## Problem Fixed: Excel Upload from Android Device

### Issues Identified and Resolved:

## 🔴 **Issue 1: API Endpoint Mismatch**

**Problem:**
- Android app was calling: `/api/android/projects/upload`
- Backend only had: `/projects/upload`
- Result: 404 Not Found error

**Solution:** ✅
Added Android-specific endpoints to the backend:
- `/api/android/projects/upload` - For Excel file uploads
- `/api/android/projects` - For getting all projects
- `/api/android/projects/create` - For creating new projects

---

## 🔴 **Issue 2: Missing CORS Headers**

**Problem:**
Cross-Origin Resource Sharing (CORS) was not configured, which could block requests from the Android app.

**Solution:** ✅
Added CORS middleware to FastAPI:
```python
from fastapi.middleware.cors import CORSMiddleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

---

## 🔴 **Issue 3: Insufficient Logging**

**Problem:**
No detailed logging made it difficult to debug upload failures.

**Solution:** ✅
Added comprehensive logging:
- File upload received (filename, content type)
- File size
- Parsing status
- Database operations
- Error details with stack traces

---

## ✅ **What Was Fixed in Backend**

### File: `backend/DailyActivityRegisterBackend/scripts/main.py`

1. **Added CORS Support** - Allows Android app to make HTTP requests
2. **Added Android Endpoints** - Matches what the app expects
3. **Enhanced Logging** - Better debugging information
4. **Added Debug Endpoint** - `/api/android/debug/upload` to test file uploads
5. **Better Error Handling** - More descriptive error messages

---

## 🚀 **How to Test the Fix**

### Step 1: Restart the Backend Server

```bash
cd backend/DailyActivityRegisterBackend/scripts
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

**Important:** Use `--host 0.0.0.0` to allow connections from other devices on your network!

### Step 2: Verify Server is Running

Open a browser and go to:
- `http://10.223.19.245:8000/` - Should show: `{"status": "ok", "message": "Daily Activity API is running"}`
- `http://10.223.19.245:8000/docs` - Should show the FastAPI interactive documentation

### Step 3: Test from Android Device

1. Make sure your Android device is connected to the same WiFi
2. Open the app
3. Try uploading an Excel file
4. Check the backend terminal for log messages

---

## 🔍 **Debugging Steps if Upload Still Fails**

### Check 1: Network Connectivity

From your Android device, open a browser and navigate to:
```
http://10.223.19.245:8000/
```

If this doesn't load, the issue is network connectivity:
- Verify both devices are on the same WiFi
- Check if your PC's firewall is blocking port 8000
- Try disabling Windows Firewall temporarily to test

### Check 2: Check Backend Logs

When you upload a file, you should see logs like:
```
INFO: Received file upload: project.xlsx, content_type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
INFO: File size: 12345 bytes
INFO: Parsed project: My Project Name
INFO: Created new project: My Project Name
```

If you don't see these logs, the request isn't reaching the server.

### Check 3: Use the Debug Endpoint

The backend now has a debug endpoint. You can test it from the Android app by temporarily changing the upload URL in `MainActivity.kt`:

```kotlin
// Temporarily change this line for debugging
RetrofitInstance.api.debugUpload(part)  // Instead of uploadProjectFile
```

This will return information about what the server received.

### Check 4: Check File Picker

Make sure the Android app is picking the correct file type. In `MainActivity.kt`, the file picker uses:
```kotlin
excelFilePickerLauncher.launch("*/*")
```

You might want to be more specific:
```kotlin
excelFilePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
```

---

## 🔧 **Common Issues and Solutions**

### Issue: "Connection refused" or "Unable to resolve host"

**Cause:** Backend server not running or wrong IP address

**Solution:**
1. Make sure backend is running with `--host 0.0.0.0`
2. Verify the IP address in `RetrofitInstance.kt` matches your PC's IP
3. Get your PC's IP with: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)

### Issue: "Failed to parse Excel"

**Cause:** Excel file format not supported or corrupted

**Solution:**
1. Make sure the Excel file is in `.xlsx` format (not `.xls`)
2. Verify the Excel file has the expected structure
3. Check backend logs for specific parsing errors

### Issue: "Upload failed: 500 Internal Server Error"

**Cause:** Backend error (database, parsing, etc.)

**Solution:**
1. Check backend terminal for error stack trace
2. Verify PostgreSQL database is running
3. Check database connection string in `main.py`

---

## 📱 **Android App Configuration**

### Current Backend URL

File: `app/src/main/java/com/example/dailyactivityregister/network/RetrofitInstance.kt`

```kotlin
private const val BASE_URL = "http://10.223.19.245:8000/"
```

**Make sure this IP matches your PC's current IP address!**

To find your PC's IP:
```bash
ipconfig
```
Look for "IPv4 Address" under your WiFi adapter.

---

## 🎯 **Expected Behavior After Fix**

1. **User selects Excel file** → App shows file picker
2. **User picks file** → App reads file and creates multipart request
3. **App uploads to backend** → Backend receives file and logs it
4. **Backend parses Excel** → Extracts project data
5. **Backend saves to database** → PostgreSQL stores the project
6. **Backend returns project** → App receives the created/updated project
7. **App refreshes list** → User sees the new project

---

## 📊 **API Endpoints Reference**

### Upload Excel File
```
POST /api/android/projects/upload
Content-Type: multipart/form-data
Body: file (Excel file)
```

### Get All Projects
```
GET /api/android/projects
```

### Create New Project
```
POST /api/android/projects/create
Content-Type: application/json
Body: {
  "project_name": "string",
  "project_number": "string",
  "total_route_oh": 0.0,
  "total_route_ug": 0.0,
  "line_passing_villages": "string"
}
```

### Debug Upload (for testing)
```
POST /api/android/debug/upload
Content-Type: multipart/form-data
Body: file (any file)
```

---

## ✅ **Checklist Before Testing**

- [ ] Backend server is running with `--host 0.0.0.0`
- [ ] PostgreSQL database is running
- [ ] Android device and PC are on the same WiFi
- [ ] IP address in `RetrofitInstance.kt` is correct
- [ ] Windows Firewall allows port 8000 (or temporarily disabled)
- [ ] Backend shows "Application startup complete" message
- [ ] Can access `http://YOUR_IP:8000/` from phone's browser

---

## 🆘 **Still Having Issues?**

If uploads still fail after following this guide:

1. **Check the backend logs** - They now have detailed information
2. **Test the debug endpoint** - See what the server is receiving
3. **Try from browser first** - Use the FastAPI docs at `/docs` to test upload
4. **Check network** - Make sure you can ping the server from your phone

---

**Last Updated:** 2025-11-30
**Status:** ✅ Fixed and Ready to Test
