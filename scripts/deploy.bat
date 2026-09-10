@echo off
REM ==============================================================
REM Script Trien Khai Production (Windows PowerShell / CMD)
REM ==============================================================

setlocal enabledelayedexpansion

set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env.production

if not exist "%ENV_FILE%" (
    if exist ".env" (
        set ENV_FILE=.env
    ) else (
        echo [CANH BAO] Khong tim thay file .env.production hoac .env!
    )
)

echo ==============================================================
echo [1/2] Khoi chay Database va Backend services...
echo ==============================================================
docker compose -f %COMPOSE_FILE% up -d --remove-orphans

if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Khong the start cac container!
    exit /b %ERRORLEVEL%
)

echo ==============================================================
echo [2/2] Kiem tra do on dinh cua Backend qua Actuator...
echo ==============================================================
call "%~dp0healthcheck.bat"

echo ==============================================================
echo [HOAN TAT] Trien khai thanh cong!
echo ==============================================================
