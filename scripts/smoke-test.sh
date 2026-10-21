#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${FORGEFLOW_API_URL:-http://localhost:8080}"
OUT="${1:-./FF-smoke-report.csv}"
KEY="forgeflow-smoke-$(date +%s)"

echo "Submitting sample report to ${BASE_URL}/api/jobs ..."
RESPONSE=$(curl -fsS -X POST "${BASE_URL}/api/jobs" \
  -H 'Content-Type: application/json' \
  -H "Idempotency-Key: ${KEY}" \
  -d "{\"type\":\"GENERATE_REPORT\",\"payload\":{\"reportType\":\"SALES_SUMMARY\",\"lookbackDays\":7,\"dataLabel\":\"SAMPLE DATA ONLY\"},\"priority\":\"NORMAL\",\"scheduleMode\":\"NOW\",\"maxAttempts\":3,\"idempotencyKey\":\"${KEY}\"}")
echo "$RESPONSE" | python3 -m json.tool
JOB_ID=$(python3 -c 'import json,sys; print(json.load(sys.stdin)["id"])' <<< "$RESPONSE")
echo "Waiting for job ${JOB_ID} ..."
for _ in $(seq 1 60); do
  STATE=$(curl -fsS "${BASE_URL}/api/jobs/${JOB_ID}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["status"])')
  echo "${JOB_ID}: ${STATE}"
  if [[ "$STATE" == "SUCCEEDED" ]]; then
    curl -fsS "${BASE_URL}/api/jobs/${JOB_ID}/result" -o "$OUT"
    echo "Report downloaded to ${OUT}"
    exit 0
  fi
  if [[ "$STATE" == "DEAD" ]]; then echo "Job died; inspect docker compose logs." >&2; exit 1; fi
  sleep 1
done
echo "Timed out waiting for job; inspect docker compose logs." >&2
exit 1
