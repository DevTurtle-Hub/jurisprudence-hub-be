#!/bin/bash
# ==============================================================
# Healthcheck Script for Jurisprudence Hub Backend (Actuator)
# ==============================================================

PORT="${HOST_PORT:-${PORT:-8081}}"
HEALTH_URL="http://localhost:${PORT}/actuator/health"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"
SLEEP_SECONDS="${SLEEP_SECONDS:-3}"

echo "============================================================="
echo "Dang kiem tra tinh trang hoat dong tai: ${HEALTH_URL}"
echo "So lan thu toi da: ${MAX_ATTEMPTS} (moi lan cach nhau ${SLEEP_SECONDS}s)"
echo "============================================================="

attempt=1
while [ "$attempt" -le "$MAX_ATTEMPTS" ]; do
    echo "[Lan $attempt/$MAX_ATTEMPTS] Dang goi $HEALTH_URL ..."
    
    # Su dung curl hoac wget neu co
    if command -v curl >/dev/null 2>&1; then
        HTTP_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" 2>/dev/null || echo "000")
        RESPONSE_BODY=$(curl -s "$HEALTH_URL" 2>/dev/null || echo "")
    elif command -v wget >/dev/null 2>&1; then
        HTTP_RESPONSE=$(wget -q -S -O - "$HEALTH_URL" 2>&1 | awk '/HTTP\// {print $2}' | tail -1 || echo "000")
        RESPONSE_BODY=$(wget -q -O - "$HEALTH_URL" 2>/dev/null || echo "")
    else
        echo "[CANH BAO] Khong tim thay curl hoac wget tren he thong!"
        exit 1
    fi

    echo "HTTP Status Code: $HTTP_RESPONSE"
    
    if [ "$HTTP_RESPONSE" = "200" ] && [[ "$RESPONSE_BODY" == *"UP"* ]]; then
        echo "============================================================="
        echo "[THANH CONG] Backend da khoi dong UP va san sang phuc vu!"
        echo "Chi tiet phan hoi: $RESPONSE_BODY"
        echo "============================================================="
        exit 0
    fi

    echo "Ung dung dang khoi dong, doi ${SLEEP_SECONDS} giay..."
    sleep "$SLEEP_SECONDS"
    attempt=$((attempt + 1))
done

echo "============================================================="
echo "[THAT BAI] Backend khong the khoi dong sau $((MAX_ATTEMPTS * SLEEP_SECONDS)) giay!"
echo "Vui long kiem tra container logs:"
docker logs --tail 50 jurisprudence-backend 2>/dev/null || true
echo "============================================================="
exit 1
