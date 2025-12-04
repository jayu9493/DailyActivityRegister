"""
Patch main.py to use environment variables for cloud deployment
"""

# Read the file
with open('main.py', 'r', encoding='utf-8') as f:
    content = f.read()

# Add os import if not present
if 'import os' not in content:
    # Find the first import line and add os import after pandas
    content = content.replace('import pandas as pd', 'import pandas as pd\nimport os')

# Replace DATABASE_URL line
old_line = '    DATABASE_URL = "postgresql://postgres:admin@localhost:5432/daily_activity_db"'
new_line = '    DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://postgres:admin@localhost:5432/daily_activity_db")'

content = content.replace(old_line, new_line)

# Write back
with open('main.py', 'w', encoding='utf-8') as f:
    f.write(content)

print("✓ main.py patched successfully!")
print("✓ Added: import os")
print("✓ Updated: DATABASE_URL to use environment variable")
