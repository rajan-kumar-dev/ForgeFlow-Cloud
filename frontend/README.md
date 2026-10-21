# ForgeFlow frontend

React + TypeScript + Vite UI for ForgeFlow Cloud.

- Landing/sign-in page inspired by the supplied design screenshot.
- Clerk sign-in/sign-up and Google OAuth when configured.
- Explicit local-preview entry when Clerk is not configured (not an authentication mechanism).
- Workspace with job submission, job explorer, status refresh, report download and Failure Lab.
- Vite proxies `/api` to the Spring Cloud Gateway at `http://localhost:8080`.

Run as part of the full platform from repository root using Docker Compose; see the root [`README.md`](../README.md).

For frontend-only development:

```bash
npm install
cp .env.example .env.local
npm run dev
```
