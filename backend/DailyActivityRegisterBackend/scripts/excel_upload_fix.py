# Quick fix: Add this to handle Excel uploads
# This is a minimal working version - replace main.py if it's corrupted

import pandas as pd
import io
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Daily Activity API - Excel Upload Fixed")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"message": "Excel upload server running", "status": "ok"}

@app.post("/api/android/projects/upload")
async def upload_excel(file: UploadFile = File(...)):
    """Handle Excel file upload from Android app"""
    try:
        contents = await file.read()
        df = pd.read_excel(io.BytesIO(contents), header=None, engine='openpyxl').fillna('')
        
        project_info = {}
        for index, row in df.iterrows():
            if index > 20:
                break
            key = str(row[0]).strip()
            if key and len(row) > 1:
                project_info[key] = str(row[1]).strip()
        
        project_name = project_info.get("Name of Project", "Unknown Project")
        
        logger.info(f"Successfully parsed Excel file: {project_name}")
        
        return {
            "project_name": project_name,
            "project_number": project_info.get("Project Number", ""),
            "message": "Excel file parsed successfully"
        }
        
    except Exception as e:
        logger.error(f"Excel upload error: {e}")
        raise HTTPException(status_code=400, detail=f"Failed to parse Excel: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)  # Use port 8001 to not conflict
