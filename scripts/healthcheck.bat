@echo off
REM ==============================================================
REM Healthcheck Script cho Windows (Spring Boot Actuator)
REM ==============================================================

set PORT=%PORT%
if "%PORT%"=="" set PORT=8080

set HEALTH_URL=http://localhost:%PORT%/actuator/health
set MAX_ATTEMPTS=30
set SLEEP_SECONDS=3

echo Dang kiem tra: %HEALTH_URL%

for /L %%i in (1,1,%MAX_ATTEMPTS%) do (
    echo [Lan %%i/%MAX_ATTEMPTS%] Kiem tra backend...
    powershell -Command "try { $res = Invoke-RestMethod -Uri '%HEALTH_URL%' -TimeoutSec 3; if ($res.status -eq 'UP') { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo [THANH CONG] Backend da san sang tai port %PORT%!
        exit /b 0
    )
    timeout /t %SLEEP_SECONDS% /nobreak >nul
)

echo [THAT BAI] Backend khong the UP sau thoi gian cho!
docker logs --tail 30 jurisprudence-backend
exit /b 1
