# Kubernetes deployment starter

This guide targets a local single-node Kubernetes cluster (Minikube or kind) for learning. It is not a production hardening checklist replacement.

## 1. Build and publish images

Build the app images from the repository root (Docker is required):

```bash
docker build -f services/job-service/Dockerfile -t ghcr.io/YOUR_ORG/forgeflow-job-service:latest .
docker build -f services/worker-service/Dockerfile -t ghcr.io/YOUR_ORG/forgeflow-worker-service:latest .
docker build -f services/scheduler-service/Dockerfile -t ghcr.io/YOUR_ORG/forgeflow-scheduler-service:latest .
docker build -f services/api-gateway/Dockerfile -t ghcr.io/YOUR_ORG/forgeflow-api-gateway:latest .
docker build -f frontend/Dockerfile -t ghcr.io/YOUR_ORG/forgeflow-frontend:latest \
  --build-arg VITE_API_BASE_URL=/api \
  --build-arg VITE_CLERK_PUBLISHABLE_KEY=YOUR_CLERK_PUBLISHABLE_KEY frontend
```

Replace `YOUR_ORG`, push the images to a registry accessible by your cluster, and update image names in `k8s/forgeflow.yaml`.

## 2. Set secrets before applying

Edit the `Secret` in `k8s/forgeflow.yaml` with strong unique values for Postgres, RabbitMQ, the internal service token, Grafana and the Clerk issuer URL. Never commit real secrets. For production, use your platform's secret manager and RBAC rather than committing a Secret manifest.

The Kubernetes manifest enables backend JWT validation. It will not work until the Clerk issuer value is real and the frontend image is built with a valid Clerk publishable key. Configure an admin role claim in Clerk for admin endpoints.

## 3. Apply manifests

```bash
kubectl apply -f k8s/forgeflow.yaml
kubectl apply -f k8s/monitoring.yaml
kubectl -n forgeflow get pods
kubectl -n forgeflow get services
```

The worker HPA requires Kubernetes Metrics Server. On local clusters, enable/install it if it's not present. The sample reports PVC uses `ReadWriteOnce` and is meant for a simple one-node setup; for multi-node deployments use object storage or a shared filesystem designed for that purpose.

## 4. Port-forward for a local test

```bash
kubectl -n forgeflow port-forward service/frontend 3000:80
kubectl -n forgeflow port-forward service/prometheus 9090:9090
kubectl -n forgeflow port-forward service/grafana 3001:3000
```

Then browse to `http://localhost:3000`, `http://localhost:9090`, and `http://localhost:3001`. Use the Grafana password stored in the Kubernetes secret. Add an Ingress, TLS, NetworkPolicies, persistent monitoring storage, resource requests/limits and upgrade/rollback policies before a public deployment.
