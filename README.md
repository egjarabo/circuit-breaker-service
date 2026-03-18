# circuit-breaker-service

Educational Spring Boot project implementing the **Circuit Breaker** pattern using Resilience4j.

---

## Technologies

- Java 21
- Spring Boot 3.5.11
- Spring Cloud 2025.0.1 / Resilience4j
- Spring AOP

---

## Project Structure

```
circuitbreaker/
├── config/
│   └── RestTemplateConfig.java       # RestTemplate bean
├── controller/
│   └── CircuitBreakerController.java # HTTP layer — delegates to service
├── exception/
│   └── ExternalApiException.java     # Domain exception
└── service/
    └── ExternalApiService.java       # Circuit Breaker logic lives here
```

> The `@CircuitBreaker` annotation belongs in the **service layer**, not the controller.

---

## Dependencies

### Required

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST API and RestTemplate |
| `spring-cloud-starter-circuitbreaker-resilience4j` | Circuit Breaker implementation |
| `spring-boot-starter-aop` | Required for `@CircuitBreaker` annotation processing |

## Circuit Breaker Configuration

The `externalApiConfig` circuit is configured with the following parameters:

| Parameter | Value | Description |
|---|---|---|
| `slidingWindowSize` | 5 | Last N calls evaluated to calculate failure rate |
| `minimumNumberOfCalls` | 3 | Minimum calls before the circuit can open |
| `failureRateThreshold` | 50% | Failure rate to transition to OPEN |
| `waitDurationInOpenState` | 10s | Time in OPEN before transitioning to HALF_OPEN |
| `permittedNumberOfCallsInHalfOpenState` | 2 | Test calls allowed in HALF_OPEN state |

The transition from OPEN to HALF_OPEN is automatic (`automaticTransitionFromOpenToHalfOpenEnabled: true`).

---

## Available Endpoints

With the application running at `http://localhost:8081`:

| Endpoint | Description |
|---|---|
| `GET /api/check` | Calls the external API (Circuit Breaker active) |
| `GET /api/switch?active=false` | Points to an invalid URL — simulates external service failure |
| `GET /api/switch?active=true` | Restores valid URL — simulates recovery |
| `GET /api/status` | Shows current circuit state and metrics |

---

## Run

```bash
./mvnw spring-boot:run
```

---

## Demo

To watch the circuit breaker state change in real time, run in a separate terminal:

```bash
watch -n 1 curl -s http://localhost:8081/api/status
```

Then trigger the state transitions:

1. `GET /api/switch?active=false` — point to invalid URL
2. `GET /api/check` × 3 — trigger failures → circuit opens (`OPEN`)
3. Wait 10s → circuit transitions automatically to `HALF_OPEN`
4. `GET /api/switch?active=true` — restore valid URL
5. `GET /api/check` × 2 — test calls succeed → circuit closes (`CLOSED`)
