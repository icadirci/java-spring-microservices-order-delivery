### Spring Boot Microservices — Order Delivery Platform

An educational microservices system for an order & delivery domain, built with Java 17 and Spring Boot. It demonstrates API Gateway routing, service discovery, JWT-based authentication, asynchronous communication with RabbitMQ, caching/Idempotency with Redis, and PostgreSQL persistence.


### Architecture

- **gateway-service**: Entry point via Spring Cloud Gateway (port 8080). Forwards `Authorization` header to downstream services.
- **discovery-service**: Service registry (Eureka, port 8761).
- **user-service**: User registration/login and profile endpoints, JWT issuance/validation (PostgreSQL).
- **order-service**: Creates and fetches orders for authenticated users. Propagates JWT to internal calls when needed (PostgreSQL).
- **notification-service**: Consumes user events via RabbitMQ and uses Redis for idempotency.
- **Infra**: PostgreSQL, RabbitMQ, Redis via Docker.

Service-to-service discovery is handled by Eureka; routing is centralized at the Gateway. Client Bearer tokens are forwarded by Gateway, and validated within services by Spring Security filters.


### Tech Stack

- Java 17, Maven, Spring Boot 3
- Spring Cloud Gateway, Eureka (Service Discovery)
- Spring Security (JWT with `jjwt`)
- Spring Data JPA, PostgreSQL
- RabbitMQ (AMQP), Spring AMQP
- Redis (Spring Data Redis)
- Docker Compose for local orchestration


### Modules

- `gateway-service`
- `discovery-service`
- `user-service`
- `order-service`
- `notification-service`
- `common-libs`, `common-infra`


### Quick Start (Docker)

Prerequisites: Docker & Docker Compose.

1) Build the project:

```bash
mvn clean package -DskipTests
```

2) Start the stack:

```bash
docker compose up -d --build
```

Services (host ports):
- Gateway: `http://localhost:8080`
- Discovery (Eureka UI): `http://localhost:8761`
- User service: `http://localhost:8081` (container 8080 → host 8081)
- Order service: `http://localhost:8082` (container 8080 → host 8082)
- Notification service: `http://localhost:8084` (container 8080 → host 8084)
- PostgreSQL: `localhost:5432` (user/password as in compose)
- RabbitMQ: `http://localhost:15672` (guest/guest), AMQP `localhost:5672`
- Redis: `localhost:6379`

Environment is pre-wired via `docker-compose.yml`:
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-service:8761/eureka`
- `APP_JWT_SECRET=super-secret-key-at-least-32-characters-long` (must be ≥32 chars)


### Running Locally (without Docker)

Prerequisites: Java 17, Maven, PostgreSQL, RabbitMQ, Redis.

1) Start Discovery:
```bash
mvn -pl discovery-service spring-boot:run
```
2) Start services (in separate terminals), making sure environment variables match your local infra:
```bash
mvn -pl user-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl gateway-service spring-boot:run
```
Configure (examples):
- `APP_JWT_SECRET` (≥32 chars)
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`
- `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka`


### API Overview (via Gateway at http://localhost:8080)

- Auth
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- Users
  - `GET /api/users/me` (requires Bearer token)
  - `GET /api/users/dashboard` (requires ADMIN role)
  - `GET /api/users/{id}`
- Orders
  - `POST /api/orders/create` (requires Bearer token)
  - `GET /api/orders/{id}` (requires Bearer token)


### Sample Flow

1) Register:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{ "fullName":"Test User", "email":"test@example.com", "password":"Passw0rd!" }'
```

2) Login and capture accessToken:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{ "email":"test@example.com", "password":"Passw0rd!" }' \
  | jq -r '.data.accessToken')
```

3) Call a protected endpoint:
```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

4) Create an order:
```bash
curl -X POST http://localhost:8080/api/orders/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "address":"Sanliurfa, Turkiye" }'
```


### Security Notes

- Gateway forwards `Authorization: Bearer <token>` to services unchanged.
- Each service validates JWT via a Spring Security filter and its own `JwtService` (shared secret must match).
- Keep `APP_JWT_SECRET` out of version control; use env vars or secrets managers.


### Development

- Build all modules: `mvn clean install`
- Run tests: `mvn test`
- Hot run a single service: `mvn -pl <module> spring-boot:run`


### Troubleshooting

- If JWT validation fails, ensure `APP_JWT_SECRET` is the same across services and ≥32 characters.
- If services don’t register, verify Eureka is up at `http://localhost:8761` and `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` is correct.
- Database connectivity: check `SPRING_DATASOURCE_URL` and credentials.
- RabbitMQ/Redis: ensure ports are not blocked and hosts match Docker or local setup.


### License

MIT (or choose a license and update this section).

