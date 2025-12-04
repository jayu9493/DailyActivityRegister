"""
Create Projects Table in Supabase
Run this FIRST before migrating data
"""

from supabase_config import SUPABASE_URL, SUPABASE_KEY
import requests

def create_table():
    """Create projects table in Supabase using SQL"""
    
    print("=" * 60)
    print("CREATING PROJECTS TABLE IN SUPABASE")
    print("=" * 60)
    print()
    
    # SQL to create the table
    create_table_sql = """
-- Create projects table
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

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_projects_subdivision ON projects(subdivision);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);

-- Enable Row Level Security (but allow all for now)
ALTER TABLE projects ENABLE ROW LEVEL SECURITY;

-- Create policies to allow all operations (you can restrict later)
CREATE POLICY IF NOT EXISTS "Allow all operations" ON projects
FOR ALL USING (true) WITH CHECK (true);
"""
    
    print("Instructions:")
    print("1. Go to your Supabase Dashboard")
    print("2. Click 'SQL Editor' in the left sidebar")
    print("3. Click 'New query'")
    print("4. Copy and paste the SQL below:")
    print()
    print("=" * 60)
    print(create_table_sql)
    print("=" * 60)
    print()
    print("5. Click 'Run' button")
    print("6. You should see 'Success. No rows returned'")
    print()
    print("Then run: python migrate_to_supabase.py")
    print()

if __name__ == "__main__":
    create_table()
