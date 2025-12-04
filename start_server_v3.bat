@echo off
echo Stopping any running servers...
taskkill /F /IM python.exe /T >nul 2>&1

echo.
echo Starting Daily Activity Register Backend V3.0
echo =============================================
cd backend\DailyActivityRegisterBackend\scripts
python main.py
pause
