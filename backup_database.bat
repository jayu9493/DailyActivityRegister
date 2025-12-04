@echo off
REM Database Backup Script for Cloud Migration
REM This script backs up your current PostgreSQL database

echo ============================================================
echo DATABASE BACKUP FOR CLOUD MIGRATION
echo ============================================================
echo.

set PGPASSWORD=admin
set BACKUP_DIR=database_backups
set TIMESTAMP=%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

REM Create backup directory if it doesn't exist
if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

echo Creating backups...
echo.

REM Backup schema only
echo [1/3] Backing up database schema...
pg_dump -h localhost -U postgres -d daily_activity_db --schema-only > %BACKUP_DIR%\schema_%TIMESTAMP%.sql
if %ERRORLEVEL% EQU 0 (
    echo ✓ Schema backup created: %BACKUP_DIR%\schema_%TIMESTAMP%.sql
) else (
    echo ✗ Schema backup failed!
    pause
    exit /b 1
)

echo.

REM Backup data only
echo [2/3] Backing up database data...
pg_dump -h localhost -U postgres -d daily_activity_db --data-only > %BACKUP_DIR%\data_%TIMESTAMP%.sql
if %ERRORLEVEL% EQU 0 (
    echo ✓ Data backup created: %BACKUP_DIR%\data_%TIMESTAMP%.sql
) else (
    echo ✗ Data backup failed!
    pause
    exit /b 1
)

echo.

REM Full backup
echo [3/3] Creating full backup...
pg_dump -h localhost -U postgres -d daily_activity_db > %BACKUP_DIR%\full_backup_%TIMESTAMP%.sql
if %ERRORLEVEL% EQU 0 (
    echo ✓ Full backup created: %BACKUP_DIR%\full_backup_%TIMESTAMP%.sql
) else (
    echo ✗ Full backup failed!
    pause
    exit /b 1
)

echo.
echo ============================================================
echo BACKUP COMPLETED SUCCESSFULLY!
echo ============================================================
echo.
echo Backup files saved in: %BACKUP_DIR%
echo.
echo You can now safely proceed with cloud migration.
echo.
pause
