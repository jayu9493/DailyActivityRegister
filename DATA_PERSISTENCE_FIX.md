# Fix for Data Persistence Issue

## Problem
When you update a project in the Android app, the changes appear to save successfully (HTTP 200 OK), but when you close and reopen the project, the values show as zero. This is a **data persistence bug** in the backend.

## Root Cause
The issue is in the `update_project` endpoint (`PUT /api/android/projects/{project_name}`). There are three critical problems:

1. **JSON Column Mutation Not Tracked**: SQLAlchemy doesn't automatically detect changes to JSON columns (tasks, agencies, daily_logs). When you modify these fields, SQLAlchemy doesn't know they've changed and doesn't persist them to the database.

2. **Deprecated Pydantic Method**: The code uses `.dict()` which is deprecated in Pydantic V2. It should use `.model_dump()`.

3. **Incorrect Timestamp**: The code sets `updated_at = func.now()` which assigns a SQL function object instead of an actual datetime value.

## Solution

### Step 1: Add the `flag_modified` import
At the top of `main.py`, change line 7 from:
```python
from sqlalchemy.orm import sessionmaker, declarative_base, Session
```

To:
```python
from sqlalchemy.orm import sessionmaker, declarative_base, Session, flag_modified
```

### Step 2: Fix the `update_project` endpoint
Find the `@app.put("/api/android/projects/{project_name}")` endpoint and replace it with this corrected version:

```python
@app.put("/api/android/projects/{project_name}", response_model=ProjectResponse)
async def update_project(
    project_name: str,
    project_data: ProjectResponse,
    db: Session = Depends(get_db_session)
):
    try:
        logger.info(f"Updating project: {project_name}")
        db_project = ProjectRepository.get_project_by_name(db, project_name)
        if not db_project:
            raise HTTPException(status_code=404, detail="Project not found")
        
        # Use model_dump() instead of deprecated dict()
        data_dict = project_data.model_dump(exclude_unset=True)
        
        # Update simple fields
        for key, value in data_dict.items():
            if hasattr(db_project, key) and key not in ["tasks", "agencies", "daily_logs", "created_at", "updated_at"]:
                setattr(db_project, key, value)
                
        # Update tasks - CRITICAL: Use flag_modified() to mark JSON column as changed
        if "tasks" in data_dict:
            tasks_data = [t.model_dump() if hasattr(t, 'model_dump') else t for t in data_dict["tasks"]]
            db_project.tasks = tasks_data
            flag_modified(db_project, "tasks")  # This tells SQLAlchemy the column changed!
            
        # Update agencies
        if "agencies" in data_dict:
            agencies_data = [a.model_dump() if hasattr(a, 'model_dump') else a for a in data_dict["agencies"]]
            db_project.agencies = agencies_data
            flag_modified(db_project, "agencies")
            
        # Update daily logs
        if "daily_logs" in data_dict:
            logs_data = [l.model_dump() if hasattr(l, 'model_dump') else l for t in data_dict["daily_logs"]]
            db_project.daily_logs = logs_data
            flag_modified(db_project, "daily_logs")
            
        # Use Python datetime instead of SQL function
        db_project.updated_at = datetime.now()
        
        # Explicitly commit and refresh
        db.commit()
        db.refresh(db_project)
        logger.info(f"Project updated successfully: {project_name}")
        return clean_db_project(db_project)
        
    except Exception as e:
        logger.error(f"Update failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))
```

## Why This Fixes the Problem

1. **`flag_modified(db_project, "tasks")`**: This explicitly tells SQLAlchemy that the JSON column has been modified and needs to be persisted to the database.

2. **`model_dump()`**: Uses the correct Pydantic V2 method, eliminating the deprecation warning.

3. **`datetime.now()`**: Sets the timestamp to an actual Python datetime object instead of a SQL function.

## Testing the Fix

1. Stop the current server (Ctrl+C)
2. Apply the changes above to `main.py`
3. Restart the server: `.\start_server_v3.bat`
4. In your Android app:
   - Open a project
   - Update some task values
   - Close the app completely
   - Reopen the app and check the project
   - The values should now persist correctly!

## Additional Notes

- The Pydantic warning you saw (`PydanticDeprecatedSince20`) will also be fixed by this change
- The fix ensures that ALL changes (tasks, agencies, daily logs) are properly persisted
- The database commit happens explicitly, ensuring data is saved before the response is sent

---

**Created**: 2025-12-02
**Issue**: Data not persisting after app restart
**Status**: Ready to apply
