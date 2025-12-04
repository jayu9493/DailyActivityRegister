@echo off
echo ========================================
echo Starting Daily Activity Register Backend
echo ========================================
echo.
echo Server will be accessible at:
echo   - Local: http://localhost:8000
echo   - Network: http://YOUR_IP:8000
echo   - API Docs: http://YOUR_IP:8000/docs
echo.
echo Make sure PostgreSQL is running!
echo.
echo Starting server...
echo.

cd backend\DailyActivityRegisterBackend\scripts
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload

pause
