"""
Test if subdivision is being read from database
"""

import psycopg2

DB_CONFIG = {
    'dbname': 'daily_activity_db',
    'user': 'postgres',
    'password': 'admin',
    'host': 'localhost',
    'port': '5432'
}

def check_subdivisions():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Get all projects with their subdivisions
        cursor.execute("""
            SELECT project_name, subdivision 
            FROM projects 
            WHERE status = 'active'
            ORDER BY project_name;
        """)
        
        projects = cursor.fetchall()
        
        print("=" * 60)
        print("PROJECTS AND THEIR SUBDIVISIONS")
        print("=" * 60)
        
        if not projects:
            print("No active projects found.")
        else:
            for project_name, subdivision in projects:
                sub_display = subdivision if subdivision else "(not set)"
                print(f"{project_name}: {sub_display}")
        
        print("\n" + "=" * 60)
        
        # Count by subdivision
        cursor.execute("""
            SELECT subdivision, COUNT(*) 
            FROM projects 
            WHERE status = 'active'
            GROUP BY subdivision
            ORDER BY subdivision;
        """)
        
        counts = cursor.fetchall()
        
        print("SUBDIVISION COUNTS")
        print("=" * 60)
        for subdivision, count in counts:
            sub_display = subdivision if subdivision else "(not set)"
            print(f"{sub_display}: {count} project(s)")
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    check_subdivisions()
