# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Working with this user

This is a learning project. The user prefers step-by-step explanations **in Turkish** and wants to write the code themselves — explain what to change and why rather than making edits, unless explicitly asked to implement.

## Build & run

Maven multi-module project (Java 17, Spring Boot 3.2, Spring Cloud 2023.0). No Maven wrapper — use a system `mvn`.

```bash
mvn clean package -DskipTests              # build all modules (required before docker compose: Dockerfiles copy target/*.jar)
mvn -pl user-service -am package           # build one service plus the modules it depends on (common-libs, common-infra)
mvn -pl user-service test                  # tests for one module
mvn -pl user-service test -Dtest=AuthControllerTest                 # single test class
mvn -pl user-service test -Dtest=AuthControllerTest#register_shouldCreateUser_andReturnToken  # single method
mvn -pl <module> spring-boot:run           # run one service locally
```

If `common-libs` changes (including `src/main/proto/*.proto`), rebuild/install it before building dependents: `mvn -pl common-libs install`. The gRPC stubs are generated into `common-libs/target/generated-sources/protobuf` by `protobuf-maven-plugin`.

Docker:

```bash
docker compose up -d --build                        # dev: merges docker-compose.override.yml automatically
docker compose -f docker-compose.yml up -d --build  # prod-like: only the gateway is exposed
```

`docker-compose.yml` exposes only the gateway (host **8580** → 8080). `docker-compose.override.yml` adds dev ports: Eureka 8761, user-service 8083 (gRPC 9090), order-service 8082, catalog-service 8085, notification-service 8084, Postgres 5432, RabbitMQ 5672/15672, Redis **6385**, Mailpit UI 6025 / SMTP 7025, and JDWP remote-debug ports 5006/5007/5008 (user/order/catalog). The ports in README.md are out of date; trust the compose files.

Postgres runs one instance with a DB per service (`user_db`, `order_db`, `catalog_db`, `notification_db`), created by scripts in `docker/postgres/init/` (these only run on a fresh `pgdata` volume). Schemas come from `ddl-auto: update`, not migrations.

## Architecture

Modules: `gateway-service`, `discovery-service` (Eureka), `user-service`, `order-service`, `catalog-service`, `notification-service`, plus shared jars `common-libs` and `common-infra`.

### Authentication flow (gateway-centric)

JWTs are issued by user-service (`auth/AuthService`, `security/JwtService`) and **validated only at the gateway**:

1. `gateway-service/.../security/JwtAuthGlobalFilter` (a `GlobalFilter` at highest precedence) first strips every client-supplied header starting with `X-Auth-`, then — deny by default, except the `PUBLIC_ENDPOINTS` list (login/register) — validates the Bearer token and sets `X-Auth-UserId`, `X-Auth-Email`, `X-Auth-Role` from the claims.
2. Downstream services do **not** parse JWTs. Each has its own `security/GatewayHeaderAuthFilter` that builds the Spring `Authentication` from those headers (principal = user id string, authority = `ROLE_<role>`), and a stateless `SecurityConfig` requiring authentication except `/actuator/**`.
3. Header names live in `common-libs` `com.orderplatform.common.security.AuthHeaders`; the `X-Auth-` prefix contract is what makes header stripping safe. Adding a new public route means updating `PUBLIC_ENDPOINTS` in the gateway filter.

The gateway's own Spring Security config only exposes `/actuator/health`; other gateway actuator endpoints are denied. `APP_JWT_SECRET` (≥32 chars) must match between gateway and user-service.

The gateway excludes all `io.grpc` artifacts from `common-libs` on purpose — gRPC on the classpath activates Spring Cloud Gateway's gRPC support and breaks startup. Keep that exclusion when touching the gateway POM.

### Routing

Routes are static in `gateway-service/src/main/resources/application.yaml` (discovery locator disabled), using `lb://<service>` via Eureka: `/api/auth/**` and `/api/users/**` → user-service, `/api/orders/**` → order-service, `/api/products/**` and `/api/categories/**` → catalog-service, `/api/notifications/**` → notification-service. New endpoints under a new prefix need a new route here.

### Inter-service communication

- **gRPC (sync)**: user-service hosts `grpc/UserGrpcService` (net.devh grpc-server starter, port 9090) implementing `UserServiceGrpcNav` from `common-libs/src/main/proto/user.proto`. order-service (and catalog-service config) call it via `@GrpcClient("userGrpcClient")`, configured as `static://user-service:9090` — this hostname only resolves inside the Docker network, so running order-service outside Docker requires overriding `grpc.client.userGrpcClient.address`.
- **RabbitMQ (async)**: user-service publishes `UserRegisteredEvent` (`common-libs` `common.event`) to topic exchange `user.exchange` with routing key `user.registered`. notification-service declares the exchange/queues/bindings (`config/RabbitConfig`): `notification.user.registered` (dead-letters to `notification.user.registered.dlq`) and `analytics.user.registered`. Both sides use `Jackson2JsonMessageConverter`, so events are JSON records shared through `common-libs`.
- notification-service de-duplicates events with a Redis key `event:<eventId>` (1-day TTL) and sends mail through Mailpit. Listener retries are configured in its `application.yaml` (3 attempts, exponential backoff); an email ending in `@fail.com` deliberately throws to exercise retry/DLQ.

### Shared modules

- `common-libs` (plain jar, no Spring Boot starters): `ApiResponse` envelope (`success`/`data`/`message`) used for all HTTP responses, `BaseException`/`ErrorCode`, `Role`, `AuthHeaders`, event records, and the proto/gRPC generated code.
- `common-infra`: servlet-side infrastructure — `TraceIdFilter` (reads/creates a trace id header, puts `traceId` into MDC; services' log patterns print `%X{traceId}`) and `UuidV7Entity` base class for JPA entities. Note the tracing sources sit in a directory literally named `src/main/java/com.orderplatform.infra/` (not nested folders); the package declaration is still `com.orderplatform.infra.tracing`.

### Service layout convention

Each service package is `com.orderplatform.<service>` (gateway uses `gateway_service`), organized by feature (`auth/`, `user/`, `order/`, `product/`, `category/`) with `controller`/`service`/`repository`/`entity`/`dto` inside, plus `security/`, `config/` (incl. `JpaAuditingConfig`), and a `GlobalExceptionHandler` mapping domain exceptions to `ApiResponse` failures.

## Tests

Tests currently exist only in user-service (`src/test/java/.../integration/auth/`): `@SpringBootTest` + MockMvc with `@ActiveProfiles("test")`, asserting on the `ApiResponse` JSON shape (`$.success`, `$.data.*`). order-service has H2 as a test dependency.
