from sqlalchemy import create_engine, text
import json
import os

DATABASE_URL = "postgresql://postgres:admin@localhost:5432/daily_activity_db"
engine = create_engine(DATABASE_URL)

backup_path = os.path.join(os.path.dirname(__file__), '..', 'projects_backup.json')

alter_statements = [
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS suborder_number VARCHAR;",
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS ug_line_length DOUBLE PRECISION DEFAULT 0.0;",
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS oh_line_length DOUBLE PRECISION DEFAULT 0.0;",
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT now();",
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS agencies JSON;",
    "ALTER TABLE projects ADD COLUMN IF NOT EXISTS tasks JSON;",
]

print(f"Backing up `projects` table to: {backup_path}")
with engine.connect() as conn:
    try:
        res = conn.execute(text("SELECT * FROM projects"))
        rows = []
        for r in res:
            try:
                rows.append(dict(r._mapping))
            except Exception:
                rows.append(dict(r))
        with open(backup_path, 'w', encoding='utf-8') as f:
            json.dump(rows, f, default=str, indent=2)
        print(f"Wrote {len(rows)} rows to backup.")
    except Exception as e:
        print(f"Warning: could not read `projects` table for backup: {e}")

print("Applying ALTER TABLE statements...")
with engine.begin() as conn:
    for stmt in alter_statements:
        print("Executing:", stmt)
        conn.execute(text(stmt))

print("Migration complete.")
