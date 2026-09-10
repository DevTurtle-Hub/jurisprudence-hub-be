#!/bin/bash
# ==============================================================
# Script khoi dong Jenkins CI/CD Container tren Linux/macOS
# ==============================================================
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== [1/3] Khoi chay Jenkins Server qua Docker Compose... ==="
docker compose -f "${SCRIPT_DIR}/docker-compose.jenkins.yml" up -d --build

echo "=== [2/3] Dang doi Jenkins khoi dong (15s)... ==="
sleep 15

echo "=== [3/3] Lay mat khau quan tri ban dau (Initial Admin Password): ==="
echo "-------------------------------------------------------------"
docker exec jurisprudence-jenkins cat /var/jenkins_home/secrets/initialAdminPassword || echo "Vui long doi them vai giay roi chay: docker exec jurisprudence-jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
echo "-------------------------------------------------------------"
echo ""
echo "Jenkins URL: http://localhost:8088"
