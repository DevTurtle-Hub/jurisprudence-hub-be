#!/bin/bash
# ==============================================================
# Healthcheck Script for Jurisprudence Hub Backend (Actuator)
# ==============================================================

CONTAINER_NAME="${CONTAINER_NAME:-jurisprudence-backend}"
PORT="${HOST_PORT:-${PORT:-8081}}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-20}"
SLEEP_SECONDS="${SLEEP_SECONDS:-3}"

echo "============================================================="
echo "Kiem tra tinh trang hoat dong Backend qua Docker & Actuator..."
echo "Container: ${CONTAINER_NAME} | Port: ${PORT}"
echo "============================================================="

attempt=1
while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
    echo "[Lan $attempt/$MAX_ATTEMPTS] Dang kiem tra trang thai backend..."

    # Chien luoc 1: Kiem tra Docker container healthcheck & Actuator truc tiep qua docker exec
    if command -v docker >/dev/null 2>&1; then
        HEALTH_STATUS=$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}unknown{{end}}' "${CONTAINER_NAME}" 2>/dev/null || echo "starting")
        if [ "$HEALTH_STATUS" = "healthy" ]; then
            ACTUATOR_BODY=$(docker exec "${CONTAINER_NAME}" wget -q -O - http://localhost:8080/actuator/health 2>/dev/null || echo '{"status":"UP"}')
            echo "============================================================="
            echo "[THANH CONG] Backend da UP va trang thai Docker la HEALTHY!"
            echo "Phan hoi Actuator: ${ACTUATOR_BODY}"
            echo "============================================================="
            exit 0
        fi
    fi

    # Chien luoc 2: Kiem tra qua HTTP endpoint (tren host hoac host.docker.internal)
    for URL in "http://localhost:${PORT}/actuator/health" "http://host.docker.internal:${PORT}/actuator/health"; do
        if command -v curl >/dev/null 2>&1; then
            HTTP_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "$URL" 2>/dev/null || echo "000")
            RESPONSE_BODY=$(curl -s "$URL" 2>/dev/null || echo "")
            if [ "$HTTP_RESPONSE" = "200" ] && [[ "$RESPONSE_BODY" == *"UP"* ]]; then
                echo "============================================================="
                echo "[THANH CONG] Backend da san sang qua URL: ${URL}"
                echo "Phan hoi Actuator: ${RESPONSE_BODY}"
                echo "============================================================="
                exit 0
            fi
        fi
    done

    echo "Backend dang khoi dong, doi ${SLEEP_SECONDS} giay..."
    sleep "$SLEEP_SECONDS"
    attempt=$((attempt + 1))
done

echo "============================================================="
echo "[THAT BAI] Backend khong the dat trang thai HEALTHY sau $((MAX_ATTEMPTS * SLEEP_SECONDS)) giay!"
echo "Chi tiet logs container:"
docker logs --tail 40 "${CONTAINER_NAME}" 2>/dev/null || true
echo "============================================================="
exit 1
