from sqlalchemy import create_engine, text

DATABASE_URL = "postgresql://postgres:admin@localhost:5432/daily_activity_db"
engine = create_engine(DATABASE_URL)

with engine.connect() as conn:
    try:
        res = conn.execute(text("SELECT project_name, suborder_number, ug_line_length, oh_line_length, created_at FROM projects LIMIT 5"))
        rows = res.fetchall()
        if not rows:
            print("No rows returned from projects table (table may be empty).")
        else:
                for r in rows:
                    try:
                        print(dict(r._mapping))
                    except Exception:
                        # Fallback for older SQLAlchemy row types
                        print(dict(r))
    except Exception as e:
        print("Verification query failed:", e)
