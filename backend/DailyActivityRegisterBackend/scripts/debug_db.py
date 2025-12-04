from main import SessionLocal, ProjectDB, clean_db_project
import logging

# Setup logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def inspect_project():
    db = SessionLocal()
    try:
        project_name = "66 kV, 220 Nani Khakar to 66 kV Koday Line"
        project = db.query(ProjectDB).filter(ProjectDB.project_name == project_name).first()
        
        if project:
            print(f"Found project: {project.project_name}")
            print(f"Daily Logs type: {type(project.daily_logs)}")
            print(f"Daily Logs content: {project.daily_logs}")
            
            # Try to clean it
            try:
                cleaned = clean_db_project(project)
                print("Successfully cleaned project data")
            except Exception as e:
                print(f"Error cleaning project: {e}")
                
        else:
            print("Project not found")
            
    except Exception as e:
        print(f"Database error: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    inspect_project()
