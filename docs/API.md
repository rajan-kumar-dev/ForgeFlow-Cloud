# ForgeFlow API quick reference

The browser talks to the API Gateway (`http://localhost:8080`) in local development. The Docker-hosted UI at `http://localhost:3000` proxies `/api` to the gateway. The Job Service is not intended to be exposed directly in production.

## Submit a report job

```http
POST /api/jobs
Content-Type: application/json
Idempotency-Key: report-001
```

```json
{
  "type": "GENERATE_REPORT",
  "payload": { "reportType": "SALES_SUMMARY", "lookbackDays": 30, "format": "CSV", "dataLabel": "SAMPLE DATA ONLY" },
  "priority": "NORMAL",
  "scheduleMode": "NOW",
  "maxAttempts": 3,
  "idempotencyKey": "report-001"
}
```

Response: `202 Accepted`, with a job ID and `QUEUED` (or `SCHEDULED`) status. Supported job types in this starter are `GENERATE_REPORT` and `SIMULATE_FAILURE`.

## Public, owner-scoped endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/jobs` | Submit a job |
| GET | `/api/jobs` | List your recent jobs |
| GET | `/api/jobs/{id}` | Get job status |
| GET | `/api/jobs/{id}/attempts` | List execution attempts |
| GET | `/api/jobs/{id}/result` | Download a completed CSV report |
| POST | `/api/jobs/{id}/cancel` | Cancel a queued/scheduled/retry-wait job |
| GET | `/api/dashboard/summary` | Owner-scoped status counts |

## Admin endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/admin/summary` | Global status counts |
| GET | `/api/admin/jobs` | Recent jobs across owners |
| GET | `/api/admin/workers` | Worker heartbeat registry |
| POST | `/api/admin/jobs/{id}/retry` | Requeue a dead job with a fresh attempt budget |

When `FORGEFLOW_SECURITY_ENABLED=true`, admin routes require a JWT role claim `role=admin` (or `org_role=admin`). Configure this claim in your Clerk JWT/session token. Local development defaults to an explicitly insecure no-auth mode and must not be exposed to public networks.

## Internal service endpoints

The worker and scheduler call `/internal/**` endpoints and provide `X-Internal-Token`. These paths are intentionally not routed by the API Gateway.

- `POST /internal/jobs/{id}/claim?workerId=...`
- `POST /internal/jobs/{id}/complete`
- `POST /internal/jobs/{id}/fail`
- `POST /internal/workers/heartbeat`
- `POST /internal/scheduler/dispatch-due`

## States and retry behavior

`SCHEDULED → QUEUED → RUNNING → SUCCEEDED` or `RUNNING → RETRY_WAIT → QUEUED`; exhausted retries become `DEAD`. Jobs can be cancelled before execution while in a cancellable state. The retry delay is exponential and capped (2s, 4s, 8s, … up to 60s). The attempt counter includes the first execution.

For testing retries, submit `SIMULATE_FAILURE` with `payload: { "failFirstAttempts": 2 }` and `maxAttempts: 3`. The third attempt should succeed.
