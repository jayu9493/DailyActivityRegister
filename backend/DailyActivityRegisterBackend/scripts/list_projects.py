from main import SessionLocal, ProjectDB

def list_projects():
    db = SessionLocal()
    try:
        projects = db.query(ProjectDB).all()
        print(f"Total projects: {len(projects)}")
        for p in projects:
            print(f"Name: '{p.project_name}'")
    finally:
        db.close()

if __name__ == "__main__":
    list_projects()
