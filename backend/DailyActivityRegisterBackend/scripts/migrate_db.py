from sqlalchemy import create_engine, text
import logging

# Database Configuration
DATABASE_URL = "postgresql://postgres:admin@localhost:5432/daily_activity_db"
engine = create_engine(DATABASE_URL)

def migrate_database():
    print("Starting database migration...")
    
    with engine.connect() as connection:
        # 1. Add line_passing_villages column
        try:
            print("Attempting to add 'line_passing_villages' column...")
            connection.execute(text("ALTER TABLE projects ADD COLUMN IF NOT EXISTS line_passing_villages VARCHAR;"))
            print("✅ Added 'line_passing_villages' column.")
        except Exception as e:
            print(f"⚠️ Note on line_passing_villages: {e}")

        # 2. Add daily_logs column
        try:
            print("Attempting to add 'daily_logs' column...")
            connection.execute(text("ALTER TABLE projects ADD COLUMN IF NOT EXISTS daily_logs JSON DEFAULT '[]'::json;"))
            print("✅ Added 'daily_logs' column.")
        except Exception as e:
            print(f"⚠️ Note on daily_logs: {e}")
            
        # Commit the changes
        connection.commit()
        print("🎉 Migration completed successfully!")

if __name__ == "__main__":
    migrate_database()
