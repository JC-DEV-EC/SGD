# AGENTS.md

This file provides guidance to AI agents when working with code in this repository.

## Project Overview

Backend API for **SGD — Sistema Gestor de Deudores**: a client debt management system.
Built with Spring Boot 3.2.5 and Java 21, using PostgreSQL (Supabase) as the database.
Consumed by a mobile client. Deployed via Docker on Render.

## Build & Run Commands

```powershell
# Build (skip tests)
mvn clean package -DskipTests

# Run locally (credentials read from .env via spring.config.import)
mvn spring-boot:run

# Run tests
mvn test
```

## Required Environment Variables

Set in `.env` (local, gitignored) or as environment variables (Render):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<pooler-host>:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres.<project-ref>
SPRING_DATASOURCE_PASSWORD=<password>
```

Uses the Supabase **session pooler** (port 5432 on the pooler host): compatible with prepared statements, recommended for HikariCP.

## Architecture

Three-layer architecture under `com.sgd`:

```
web/        → REST controllers, DTOs, request records
service/    → Business logic, transaction management
domain/     → JPA entities, Spring Data repositories
config/     → CORS, security headers, OpenAPI/Swagger
```

### Key Components

- **Cliente** (`domain/`): JPA entity mapped to `clientes` table (first_name, last_name, city, debt, payment, discount, status).
- **ClienteService** (`service/`): `recalculateFinancials()` computes `totalAmount = debt - payment` (clamped to 0 minimum, 10% discount if enabled) and sets ACTIVE/CANCELLED status.
- **UserApproval** (`domain/`): mapped to `user_approvals` — mobile users register with a Firebase UID and wait for ADMIN approval.

### API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/clients | List all clients |
| GET | /api/clients/{id} | Get single client |
| POST | /api/clients | Create client |
| PUT | /api/clients/{id} | Update client |
| DELETE | /api/clients/{id} | Delete client |
| POST | /api/clients/{id}/charges | Add charge to debt |
| POST | /api/clients/{id}/payments | Register payment |
| POST | /api/auth/register | Register user (firebaseUid) |
| GET | /api/auth/status/{firebaseUid} | Approval status |
| GET | /api/auth/pending | Pending users (admin) |
| PUT | /api/auth/approve/{id} | Approve user (admin) |
| PUT | /api/auth/reject/{id} | Reject user (admin) |
| GET | /api/auth/users | All users (admin) |
| PUT | /api/auth/users/{id}/role?role= | Change role (admin) |
| DELETE | /api/auth/users/{id} | Delete user (admin) |

Admin endpoints require the `X-Admin-Uid` header (Firebase UID of an ADMIN user).

## Database

- Schema managed externally: run `schema.sql` (repo root) in the Supabase SQL Editor on a fresh project. `ddl-auto=none`.
- Tables: `clientes`, `user_approvals`.

## Docs & Health

- Swagger UI: `/swagger-ui/index.html` — OpenAPI JSON at `/v3/api-docs`
- `/healthz` — simple health check (returns "OK")
- `/actuator/health` — Spring Actuator health
