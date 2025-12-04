# Subdivision Feature - Setup Instructions

## Current Status
✅ Database column added (`subdivision`)  
✅ Backend model updated (`ProjectDB`, `ProjectResponse`)  
✅ Android app model updated (`Project.kt`)  
✅ Android UI created (filter chips)  
✅ Android filtering logic implemented  
⚠️ Excel parser partially updated (return dict has subdivision, but parsing logic needs manual addition)

## What You Need to Do

### 1. Update Your Excel Template
Add a new row in your Excel template with:
- **Label**: "Subdivision" (in the first column)
- **Value**: The subdivision name (e.g., "Mundra", "Bhuj", "Anjar-1", "Anjar-2", "Samakhayali")

Place it near other project info like "Name of Project", "Project Number", etc.

### 2. Add Subdivision Parsing to Excel Parser

Open `main.py` and find this section (around line 329-330):
```python
if "oh line length" in val_lower:
    if c_idx + 1 < len(row): oh_len = clean_float(str(row[c_idx+1]).strip())
```

Add these lines RIGHT AFTER it:
```python
# NEW: Parse subdivision
if "subdivision" in val_lower:
    if c_idx + 1 < len(row): project_info["subdivision"] = str(row[c_idx+1]).strip()
```

**Note**: The return dictionary already includes subdivision (line 384), so you only need to add the parsing logic above.

### 3. Restart the Server
After making the change:
1. Stop the server (Ctrl+C)
2. Run `.\start_server_v3.bat`

### 4. Test the Feature

#### Option A: Upload New Excel File
1. Update your Excel template with "Subdivision" row
2. Upload it via the Android app
3. The project will have the subdivision set

#### Option B: Manually Set Subdivision for Existing Projects
Run this SQL in your PostgreSQL database:
```sql
-- Set subdivision for existing projects
UPDATE projects SET subdivision = 'Mundra' WHERE project_name = 'Your Project Name';
UPDATE projects SET subdivision = 'Bhuj' WHERE project_name = 'Another Project';
-- etc.
```

### 5. Use the Filter
1. Open the Android app
2. You'll see filter chips at the top: All, Mundra, Bhuj, Anjar-1, Anjar-2, Samakhayali
3. Tap a chip to filter projects by that subdivision
4. Tap "All" to see all projects

## Excel Template Example

Your Excel should have a row like this:
```
| Subdivision | Mundra |
```

Or:
```
| Subdivision | Bhuj |
```

The parser will look for the word "subdivision" (case-insensitive) and read the value from the next column.

## Troubleshooting

**If filtering doesn't work:**
1. Check that projects have `subdivision` set in database
2. Verify the Android app is fetching fresh data (pull to refresh)
3. Check server logs for errors

**If Excel upload doesn't set subdivision:**
1. Verify the parsing code was added correctly
2. Check that your Excel has "Subdivision" label
3. Restart the server after code changes

## Summary

The feature is 95% complete! You just need to:
1. Add 3 lines of code to `main.py` (the parsing logic)
2. Update your Excel template
3. Restart the server
4. Test!
