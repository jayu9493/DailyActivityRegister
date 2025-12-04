# Quick Network Diagnostic Guide

## Problem: "Failed to connect" error when uploading from Android device

### Root Cause Found:
The backend server is listening on `127.0.0.1:8000` (localhost only) instead of `0.0.0.0:8000` (all network interfaces).

This means:
- ✅ Server works from your PC
- ❌ Server NOT accessible from your phone

---

## SOLUTION: Start Server with Correct Host

### Step 1: Stop the Current Server
If the server is running, press `Ctrl+C` in the terminal to stop it.

### Step 2: Start Server Correctly

**Use ONE of these methods:**

#### Method A: Use the Batch Script (Recommended)
```bash
# Just double-click this file:
start_server.bat
```

#### Method B: Manual Command
```bash
cd backend\DailyActivityRegisterBackend\scripts
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

**⚠️ CRITICAL:** You MUST include `--host 0.0.0.0`

---

## Step 3: Verify Server is Listening on All Interfaces

After starting the server, run this command in a new terminal:
```bash
netstat -an | findstr "8000"
```

You should see:
```
TCP    0.0.0.0:8000           0.0.0.0:0              LISTENING
```

**NOT:**
```
TCP    127.0.0.1:8000         0.0.0.0:0              LISTENING
```

---

## Step 4: Get Your PC's IP Address

Run this command:
```bash
ipconfig
```

Look for "Wireless LAN adapter Wi-Fi" section and find the IPv4 Address.
Example: `192.168.1.100` or `10.223.19.245`

---

## Step 5: Update Android App (if IP changed)

If your IP address is different from `10.223.19.245`, update the Android app:

**File:** `app/src/main/java/com/example/dailyactivityregister/network/RetrofitInstance.kt`

Change line 9 to your current IP:
```kotlin
private const val BASE_URL = "http://YOUR_ACTUAL_IP:8000/"
```

Then rebuild the app:
```bash
.\gradlew.bat assembleDebug
```

---

## Step 6: Test from Phone's Browser

Before testing the app, verify connectivity from your phone:

1. Open browser on your phone
2. Go to: `http://YOUR_PC_IP:8000/`
3. You should see: `{"status": "ok", "message": "Daily Activity API is running"}`

If this doesn't work:
- Check Windows Firewall
- Verify both devices on same WiFi
- Verify IP address is correct

---

## Windows Firewall Fix (if needed)

If you can't connect even with correct server settings:

### Option 1: Temporarily Disable Firewall (for testing)
1. Open Windows Security
2. Go to Firewall & network protection
3. Turn off firewall for Private networks (temporarily)
4. Test the connection
5. Turn it back on after testing

### Option 2: Add Firewall Rule (permanent solution)
Run PowerShell as Administrator:
```powershell
New-NetFirewallRule -DisplayName "FastAPI Server" -Direction Inbound -LocalPort 8000 -Protocol TCP -Action Allow
```

---

## Quick Checklist

- [ ] Server started with `--host 0.0.0.0`
- [ ] `netstat` shows `0.0.0.0:8000` (not `127.0.0.1:8000`)
- [ ] Got current IP address from `ipconfig`
- [ ] Updated `RetrofitInstance.kt` if IP changed
- [ ] Can access `http://YOUR_IP:8000/` from phone's browser
- [ ] Windows Firewall allows port 8000

---

## Expected Server Startup Output

When you start the server correctly, you should see:
```
INFO:     Uvicorn running on http://0.0.0.0:8000 (Press CTRL+C to quit)
INFO:     Started reloader process
INFO:     Started server process
INFO:     Waiting for application startup.
INFO:     Application startup complete.
```

The key is: `http://0.0.0.0:8000` (NOT `http://127.0.0.1:8000`)

---

## Still Not Working?

If you've done all the above and still can't connect:

1. **Check if PostgreSQL is running** - The backend needs the database
2. **Check backend logs** - Look for error messages when starting
3. **Try from PC browser first** - Go to `http://localhost:8000/`
4. **Ping test** - From phone, try pinging your PC's IP

---

**Most Common Issue:** Forgetting `--host 0.0.0.0` when starting the server!
