from main import SessionLocal, ProjectDB
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def delete_project():
    db = SessionLocal()
    try:
        # The project name from your previous log
        project_name = "66 kV, 220 Nani Khakar to 66 kV Koday Line"
        
        project = db.query(ProjectDB).filter(ProjectDB.project_name == project_name).first()
        
        if project:
            db.delete(project)
            db.commit()
            print(f"Successfully deleted project: {project_name}")
        else:
            print(f"Project not found: {project_name}")
            
            # Try searching with wildcard just in case
            projects = db.query(ProjectDB).filter(ProjectDB.project_name.ilike("%Koday%")).all()
            for p in projects:
                print(f"Found similar project: {p.project_name}. Deleting...")
                db.delete(p)
            db.commit()
            
    except Exception as e:
        print(f"Error: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    delete_project()
