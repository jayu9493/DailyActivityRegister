"""
Simple Database Backup Script for Cloud Migration
Exports data as JSON for easy import to Supabase
"""

import psycopg2
import json
from datetime import datetime
import os

# Database configuration
DB_CONFIG = {
    'dbname': 'daily_activity_db',
    'user': 'postgres',
    'password': 'admin',
    'host': 'localhost',
    'port': '5432'
}

def main():
    print("=" * 60)
    print("DATABASE BACKUP FOR CLOUD MIGRATION")
    print("=" * 60)
    print()
    
    try:
        # Create backup directory
        backup_dir = 'database_backups'
        if not os.path.exists(backup_dir):
            os.makedirs(backup_dir)
        
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        
        # Connect to database
        print("Connecting to database...")
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✓ Connected successfully")
        print()
        
        # Backup all projects as JSON
        print("[1/2] Backing up projects data...")
        cursor.execute("""
            SELECT 
                project_name, project_number, requisition_number, suborder_number,
                commencement_date, total_route, total_route_oh, total_route_ug,
                line_passing_villages, subdivision, agencies, tasks, daily_logs,
                created_at, updated_at, status
            FROM projects
            ORDER BY project_name;
        """)
        
        columns = [desc[0] for desc in cursor.description]
        projects = []
        
        for row in cursor.fetchall():
            project = {}
            for i, col in enumerate(columns):
                value = row[i]
                # Convert datetime to string
                if hasattr(value, 'isoformat'):
                    value = value.isoformat()
                project[col] = value
            projects.append(project)
        
        # Save as JSON
        json_file = f'{backup_dir}/projects_backup_{timestamp}.json'
        with open(json_file, 'w', encoding='utf-8') as f:
            json.dump(projects, f, indent=2, ensure_ascii=False)
        
        print(f"✓ Backup created: {json_file}")
        print(f"  Backed up {len(projects)} projects")
        print()
        
        # Also create a simple SQL file for reference
        print("[2/2] Creating SQL reference file...")
        sql_file = f'{backup_dir}/projects_backup_{timestamp}.sql'
        
        with open(sql_file, 'w', encoding='utf-8') as f:
            f.write("-- Projects Backup\n")
            f.write(f"-- Created: {datetime.now()}\n")
            f.write(f"-- Total projects: {len(projects)}\n\n")
            
            for project in projects:
                f.write(f"-- Project: {project['project_name']}\n")
                f.write(f"-- Subdivision: {project.get('subdivision', 'N/A')}\n")
                f.write(f"-- Status: {project['status']}\n\n")
        
        print(f"✓ SQL reference created: {sql_file}")
        print()
        
        cursor.close()
        conn.close()
        
        print("=" * 60)
        print("BACKUP COMPLETED SUCCESSFULLY!")
        print("=" * 60)
        print()
        print(f"Backup files saved in: {backup_dir}/")
        print()
        print("Files created:")
        print(f"  1. {os.path.basename(json_file)} - Complete data (use this for Supabase)")
        print(f"  2. {os.path.basename(sql_file)} - Reference file")
        print()
        print("Next step: Create Supabase account at https://supabase.com")
        print()
        
    except psycopg2.Error as e:
        print(f"❌ Database error: {e}")
        return 1
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())
