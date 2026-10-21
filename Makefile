.PHONY: up down logs clean build-backend build-frontend test
up:
	docker compose up --build

down:
	docker compose down

logs:
	docker compose logs -f job-service worker-service scheduler-service api-gateway

clean:
	docker compose down -v

build-backend:
	mvn -B -ntp package

build-frontend:
	cd frontend && npm install && npm run build

test:
	mvn -B -ntp verify
	cd frontend && npm install && npm run build
