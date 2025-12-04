# QUICK FIX - Manual Code Addition Required

## Problem
The `main.py` file is getting corrupted during automated edits. You need to manually add the missing PUT endpoint.

## Step 1: Add flag_modified import (Line 7)

Find this line:
```python
from sqlalchemy.orm import sessionmaker, declarative_base, Session
```

Change it to:
```python
from sqlalchemy.orm import sessionmaker, declarative_base, Session, flag_modified
```

## Step 2: Find where to add the PUT endpoint

Search for this line in your main.py:
```python
@app.get("/api/android/projects/{project_name}", response_model=ProjectResponse)
```

Right AFTER that entire function ends (after its `return clean_db_project(project)` line), add the following code:

## Step 3: Add this complete PUT endpoint

```python
@app.put("/api/android/projects/{project_name}", response_model=ProjectResponse)
async def android_update_project(
    project_name: str,
    project_data: ProjectResponse,
    db: Session = Depends(get_db_session)
):
    """
    Update project from Android app - WITH DATA PERSISTENCE FIX
    """
    try:
        logger.info(f"Updating project: {project_name}")
        db_project = ProjectRepository.get_project_by_name(db, project_name)
        if not db_project:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Project not found"
            )
        
        # Use model_dump() instead of deprecated dict()
        data_dict = project_data.model_dump(exclude_unset=True)
        
        # Update simple fields
        for key, value in data_dict.items():
            if hasattr(db_project, key) and key not in ["tasks", "agencies", "created_at", "updated_at"]:
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
            
        # Use Python datetime instead of SQL function
        db_project.updated_at = datetime.now()
        
        # Explicitly commit and refresh
        db.commit()
        db.refresh(db_project)
        logger.info(f"Project updated successfully: {project_name}")
        return clean_db_project(db_project)
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Android project update error: {e}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Could not update project: {str(e)}"
        )
```

## Step 4: Also fix the update_project_tasks function

Find the `update_project_tasks` function in the `ProjectRepository` class and change this line:
```python
db_project.updated_at = func.now()
```

To:
```python
db_project.updated_at = datetime.now()
flag_modified(db_project, "tasks")
```

## Step 5: Restart the server

1. Stop the current server (Ctrl+C in the terminal)
2. Run: `.\start_server_v3.bat`
3. Test your Android app - data should now persist!

---

**IMPORTANT**: The key fixes are:
1. `flag_modified(db_project, "tasks")` - tells SQLAlchemy to save JSON changes
2. `datetime.now()` instead of `func.now()` - uses Python datetime
3. `model_dump()` instead of `dict()` - uses Pydantic V2 method

This will fix the "data shows zero after reopening" issue!
