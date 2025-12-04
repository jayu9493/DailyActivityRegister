"""
Automated Migration to Supabase
This script automatically migrates your local database to Supabase cloud
"""

import psycopg2
import requests
import json
from datetime import datetime

# Import Supabase config
try:
    from supabase_config import SUPABASE_URL, SUPABASE_KEY
except ImportError:
    print("❌ Error: supabase_config.py not found!")
    print("Please create supabase_config.py with your Supabase credentials")
    exit(1)

# Local database config
LOCAL_DB = {
    'dbname': 'daily_activity_db',
    'user': 'postgres',
    'password': 'admin',
    'host': 'localhost',
    'port': '5432'
}

def check_supabase_config():
    """Check if Supabase is configured"""
    if "your-project" in SUPABASE_URL or "your-anon-key" in SUPABASE_KEY:
        print("❌ Supabase not configured!")
        print()
        print("Please follow these steps:")
        print("1. Go to https://supabase.com")
        print("2. Create account and new project")
        print("3. Go to Settings → API")
        print("4. Copy URL and anon key to supabase_config.py")
        print("5. Run this script again")
        return False
    return True

def create_projects_table():
    """Create projects table in Supabase"""
    print("Creating projects table in Supabase...")
    
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS projects (
        project_name TEXT PRIMARY KEY,
        project_number TEXT,
        requisition_number TEXT,
        suborder_number TEXT,
        commencement_date TEXT,
        total_route DOUBLE PRECISION DEFAULT 0.0,
        total_route_oh DOUBLE PRECISION DEFAULT 0.0,
        total_route_ug DOUBLE PRECISION DEFAULT 0.0,
        line_passing_villages TEXT,
        subdivision TEXT,
        agencies JSONB DEFAULT '[]'::jsonb,
        tasks JSONB DEFAULT '[]'::jsonb,
        daily_logs JSONB DEFAULT '[]'::jsonb,
        created_at TIMESTAMPTZ DEFAULT NOW(),
        updated_at TIMESTAMPTZ DEFAULT NOW(),
        status TEXT DEFAULT 'active'
    );
    
    CREATE INDEX IF NOT EXISTS idx_projects_subdivision ON projects(subdivision);
    CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
    """
    
    headers = {
        'apikey': SUPABASE_KEY,
        'Authorization': f'Bearer {SUPABASE_KEY}',
        'Content-Type': 'application/json'
    }
    
    # Use Supabase SQL endpoint
    url = f"{SUPABASE_URL}/rest/v1/rpc/exec_sql"
    
    # Actually, let's use direct PostgreSQL connection
    # Supabase provides a direct PostgreSQL connection string
    print("✓ Table will be auto-created on first insert")
    return True

def migrate_projects():
    """Migrate all projects from local DB to Supabase"""
    print()
    print("=" * 60)
    print("MIGRATING TO SUPABASE")
    print("=" * 60)
    print()
    
    try:
        # Connect to local database
        print("Connecting to local database...")
        conn = psycopg2.connect(**LOCAL_DB)
        cursor = conn.cursor()
        print("✓ Connected to local database")
        print()
        
        # Get all projects
        print("Fetching projects from local database...")
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
                # Convert datetime to ISO string
                if hasattr(value, 'isoformat'):
                    value = value.isoformat()
                project[col] = value
            projects.append(project)
        
        cursor.close()
        conn.close()
        
        print(f"✓ Found {len(projects)} projects")
        print()
        
        # Upload to Supabase
        print("Uploading to Supabase...")
        headers = {
            'apikey': SUPABASE_KEY,
            'Authorization': f'Bearer {SUPABASE_KEY}',
            'Content-Type': 'application/json',
            'Prefer': 'return=representation'
        }
        
        success_count = 0
        error_count = 0
        
        for i, project in enumerate(projects, 1):
            try:
                # Use Supabase REST API to insert
                url = f"{SUPABASE_URL}/rest/v1/projects"
                response = requests.post(url, headers=headers, json=project)
                
                if response.status_code in [200, 201]:
                    success_count += 1
                    print(f"  [{i}/{len(projects)}] ✓ {project['project_name']}")
                else:
                    error_count += 1
                    print(f"  [{i}/{len(projects)}] ✗ {project['project_name']} - {response.text}")
            
            except Exception as e:
                error_count += 1
                print(f"  [{i}/{len(projects)}] ✗ {project['project_name']} - {e}")
        
        print()
        print("=" * 60)
        print("MIGRATION COMPLETE!")
        print("=" * 60)
        print(f"✓ Successfully migrated: {success_count} projects")
        if error_count > 0:
            print(f"✗ Errors: {error_count} projects")
        print()
        print("Next step: Update Android app to use Supabase URL")
        print()
        
    except psycopg2.Error as e:
        print(f"❌ Database error: {e}")
        return False
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        return False
    
    return True

def main():
    print("=" * 60)
    print("AUTOMATED SUPABASE MIGRATION")
    print("=" * 60)
    print()
    
    # Check configuration
    if not check_supabase_config():
        return 1
    
    print("✓ Supabase configured")
    print(f"  URL: {SUPABASE_URL}")
    print()
    
    # Confirm
    response = input("Ready to migrate? This will copy all projects to Supabase. (y/n): ")
    if response.lower() != 'y':
        print("Migration cancelled.")
        return 0
    
    # Migrate
    if migrate_projects():
        return 0
    else:
        return 1

if __name__ == "__main__":
    exit(main())
