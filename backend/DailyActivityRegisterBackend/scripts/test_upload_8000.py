import io
import pandas as pd
import requests

# Create a simple key-value excel with required fields in first two columns
rows = [
    ["Name of Project", "Local Test Project"],
    ["Project Number", "99999"],
    ["Requistion Number", "88888"],
    ["Suborder Number", "SO-1"],
    ["Date of commenment", "2025-11-21"],
    ["", ""],
    ["Agency", ""],
]

# Create dataframe with no header
df = pd.DataFrame(rows)
buffer = io.BytesIO()
with pd.ExcelWriter(buffer, engine='openpyxl') as writer:
    df.to_excel(writer, index=False, header=False)
buffer.seek(0)

files = {'file': ('project.xlsx', buffer.getvalue(), 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')}

url = 'http://127.0.0.1:8000/projects/upload'
print('Posting to', url)
resp = requests.post(url, files=files)
print('Status:', resp.status_code)
try:
    print(resp.json())
except Exception:
    print(resp.text)
