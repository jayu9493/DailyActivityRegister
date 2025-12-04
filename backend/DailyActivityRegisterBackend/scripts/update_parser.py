"""
Quick script to add subdivision parsing to main.py
"""

import re

# Read the file
with open('main.py', 'r', encoding='utf-8') as f:
    content = f.read()

# Find the line with "oh line length" and add subdivision parsing after it
pattern = r'(if "oh line length" in val_lower:.*?clean_float\(str\(row\[c_idx\+1\]\)\.strip\(\)\))'
replacement = r'''\1
                    # NEW: Parse subdivision
                    if "subdivision" in val_lower:
                        if c_idx + 1 < len(row): project_info["subdivision"] = str(row[c_idx+1]).strip()'''

content = re.sub(pattern, replacement, content, flags=re.DOTALL)

# Write back
with open('main.py', 'w', encoding='utf-8') as f:
    f.write(content)

print("✓ Added subdivision parsing to Excel parser")
