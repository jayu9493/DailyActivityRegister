from main import SessionLocal, ProjectDB
import json

def inspect_details():
    db = SessionLocal()
    try:
        project_name = "66 kV, 220 Nani Khakar to 66 kV Koday Line"
        project = db.query(ProjectDB).filter(ProjectDB.project_name == project_name).first()
        
        if project:
            print(f"Tasks: {json.dumps(project.tasks, indent=2)}")
            print(f"Agencies: {json.dumps(project.agencies, indent=2)}")
            print(f"Daily Logs: {json.dumps(project.daily_logs, indent=2)}")
        else:
            print("Project not found")
    finally:
        db.close()

if __name__ == "__main__":
    inspect_details()
