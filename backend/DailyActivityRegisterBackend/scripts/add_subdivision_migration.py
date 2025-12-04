"""
Database Migration: Add subdivision column to projects table
Run this script once to add the subdivision column to existing database
"""

import psycopg2

# Database connection details
DB_CONFIG = {
    'dbname': 'daily_activity_db',
    'user': 'postgres',
    'password': 'admin',
    'host': 'localhost',
    'port': '5432'
}

def add_subdivision_column():
    """Add subdivision column to projects table"""
    try:
        # Connect to database
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        print("Adding subdivision column to projects table...")
        
        # Add the column (nullable, with index)
        cursor.execute("""
            ALTER TABLE projects 
            ADD COLUMN IF NOT EXISTS subdivision VARCHAR;
        """)
        
        # Create index for better query performance
        cursor.execute("""
            CREATE INDEX IF NOT EXISTS idx_projects_subdivision 
            ON projects(subdivision);
        """)
        
        # Commit the changes
        conn.commit()
        
        print("✓ Successfully added subdivision column")
        print("✓ Created index on subdivision column")
        
        # Close connection
        cursor.close()
        conn.close()
        
        print("\nMigration completed successfully!")
        print("You can now restart the server.")
        
    except psycopg2.Error as e:
        print(f"❌ Database error: {e}")
        if conn:
            conn.rollback()
    except Exception as e:
        print(f"❌ Error: {e}")
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

if __name__ == "__main__":
    print("=" * 60)
    print("DATABASE MIGRATION: Add Subdivision Column")
    print("=" * 60)
    add_subdivision_column()
