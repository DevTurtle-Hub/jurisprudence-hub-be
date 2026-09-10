#!/bin/bash
# ==============================================================================
# Script Trien Khai Production (Docker Compose Deployment)
# ==============================================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${ROOT_DIR}"

COMPOSE_FILE="docker-compose.prod.yml"
ENV_FILE="${ENV_FILE:-.env.production}"

echo "=============================================================================="
echo "Khoi dong quy trinh trien khai Jurisprudence Hub Backend..."
echo "Thoi gian: $(date)"
echo "File Compose: ${COMPOSE_FILE}"
echo "=============================================================================="

# 1. Kiem tra file cau hinh moi truong
if [ ! -f "${ENV_FILE}" ] && [ ! -f ".env" ]; then
    echo "[CANH BAO] Khong tim thay file ${ENV_FILE} hoac .env!"
    echo "Dang su dung cac bien moi truong he thong hien co..."
fi

# 2. Pull image moi nhat neu dung image tu remote registry (tuy chon)
if [ -n "${DOCKER_REGISTRY}" ] && [ -n "${DOCKER_IMAGE}" ]; then
    echo "[Buoc 1/3] Pulling Docker Image moi nhat tu Registry..."
    docker pull "${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${BUILD_TAG:-latest}" || true
fi

# 3. Trien khai cac container qua Docker Compose
echo "[Buoc 2/3] Khoi chay Database va Backend services..."
docker compose -f "${COMPOSE_FILE}" up -d --remove-orphans

# 4. Kiem tra Healthcheck
echo "[Buoc 3/3] Kiem tra ket noi va do on dinh ung dung..."
bash "${SCRIPT_DIR}/healthcheck.sh"

echo "=============================================================================="
echo "[HOAN TAT] Trien khai thanh cong ung dung Jurisprudence Hub Backend!"
echo "=============================================================================="
