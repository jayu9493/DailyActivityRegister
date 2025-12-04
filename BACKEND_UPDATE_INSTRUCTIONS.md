# Backend Update Instructions

## Adding the Update Project Endpoint

To enable two-way communication for daily progress data, add this endpoint to your `main.py` file.

### Location
Add this code after the `android_get_project` endpoint (around line 447) and before the `android_search_projects` endpoint.

### Code to Add

```python
@app.put("/api/android/projects/{project_name}", response_model=ProjectResponse)
async def android_update_project(
    project_name: str,
    project_data: dict,
    db: Session = Depends(get_db_session)
):
    """
    Update entire project including daily logs from Android app
    """
    try:
        db_project = ProjectRepository.get_project_by_name(db, project_name)
        if not db_project:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Project not found"
            )
        
        # Update all fields that are provided
        if "project_number" in project_data:
            db_project.project_number = project_data["project_number"]
        if "requisition_number" in project_data:
            db_project.requisition_number = project_data["requisition_number"]
        if "commencement_date" in project_data:
            db_project.commencement_date = project_data["commencement_date"]
        if "total_route_oh" in project_data:
            db_project.total_route_oh = project_data["total_route_oh"]
        if "total_route_ug" in project_data:
            db_project.total_route_ug = project_data["total_route_ug"]
            db_project.total_route = project_data.get("total_route_oh", 0) + project_data["total_route_ug"]
        if "line_passing_villages" in project_data:
            db_project.line_passing_villages = project_data["line_passing_villages"]
        if "agencies" in project_data:
            db_project.agencies = project_data["agencies"]
        if "tasks" in project_data:
            db_project.tasks = project_data["tasks"]
        if "daily_logs" in project_data:
            logger.info(f"Received {len(project_data['daily_logs'])} daily logs for project {project_name}")
            # TODO: Add daily_logs column to database schema if needed
            # For now, daily logs can be stored in a separate table or as part of tasks metadata
        
        db_project.updated_at = func.now()
        db.commit()
        db.refresh(db_project)
        
        logger.info(f"Updated project: {project_name}")
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

### Steps to Apply

1. Open `backend/DailyActivityRegisterBackend/scripts/main.py`
2. Find the `android_get_project` function (around line 433-447)
3. After the `return clean_db_project(project)` line, add a blank line
4. Paste the code above
5. Save the file
6. Restart your FastAPI server

### Testing the Endpoint

You can test the endpoint using curl:

```bash
curl -X PUT "http://localhost:8000/api/android/projects/YourProjectName" \
  -H "Content-Type: application/json" \
  -d '{
    "project_name": "YourProjectName",
    "tasks": [...],
    "daily_logs": [...]
  }'
```

### Database Schema Consideration

If you want to properly store daily_logs, consider adding a `daily_logs` column to the `projects` table:

```python
# In ProjectDB class
daily_logs = Column(JSON, default=[])
```

Then run a migration:

```sql
ALTER TABLE projects ADD COLUMN daily_logs JSON DEFAULT '[]';
```

### Alternative: Use Existing update-tasks Endpoint

If you prefer not to add a new endpoint, you can modify the Android app to use the existing `/api/android/projects/update-tasks` endpoint, but you'll need to transform the daily_logs data into task updates format.
