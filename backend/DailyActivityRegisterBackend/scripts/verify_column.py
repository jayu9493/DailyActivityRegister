"""
Verify subdivision column exists in database
"""

import psycopg2

DB_CONFIG = {
    'dbname': 'daily_activity_db',
    'user': 'postgres',
    'password': 'admin',
    'host': 'localhost',
    'port': '5432'
}

def verify_column():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Check if column exists
        cursor.execute("""
            SELECT column_name, data_type, is_nullable
            FROM information_schema.columns
            WHERE table_name = 'projects' AND column_name = 'subdivision';
        """)
        
        result = cursor.fetchone()
        
        if result:
            print(f"✓ Column 'subdivision' exists!")
            print(f"  Type: {result[1]}")
            print(f"  Nullable: {result[2]}")
        else:
            print("❌ Column 'subdivision' does NOT exist")
        
        # Show all columns
        cursor.execute("""
            SELECT column_name 
            FROM information_schema.columns
            WHERE table_name = 'projects'
            ORDER BY ordinal_position;
        """)
        
        print("\nAll columns in 'projects' table:")
        for row in cursor.fetchall():
            print(f"  - {row[0]}")
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    verify_column()
