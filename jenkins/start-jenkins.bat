@echo off
REM ==============================================================
REM Script khoi dong Jenkins CI/CD Container cho Jurisprudence Hub
REM ==============================================================

echo [1/3] Khoi chay Jenkins Server qua Docker Compose...
docker compose -f "%~dp0docker-compose.jenkins.yml" up -d --build

if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Khong the khoi dong Jenkins container. Vui long kiem tra Docker da bat chua.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/3] Dang doi Jenkins khoi dong hoan tat (khoang 15 giay)...
timeout /t 15 /nobreak >nul

echo.
echo [3/3] Lay mat khau quan tri ban dau (Initial Admin Password):
echo -------------------------------------------------------------
docker exec jurisprudence-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Mat khau chua san sang, ban co the chay lenh sau sau 30 giay nua:
    echo   docker exec jurisprudence-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
)
echo -------------------------------------------------------------
echo.
echo Jenkins da san sang tai: http://localhost:8088
echo.
pause
