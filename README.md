# circuit-breaker-service

Educational Spring Boot project implementing the **Circuit Breaker** pattern using Resilience4j.

## Technologies

- Java 21
- Spring Boot 3.5.11
- Spring Cloud / Resilience4j
- Spring Boot Actuator
- Micrometer

## Circuit Breaker Configuration

The `externalApiConfig` circuit is configured with the following parameters:

| Parameter | Value | Description |
|---|---|---|
| `slidingWindowSize` | 5 | Last N calls evaluated |
| `minimumNumberOfCalls` | 3 | Minimum calls before the circuit can open |
| `failureRateThreshold` | 50% | Failure rate threshold to open the circuit |
| `waitDurationInOpenState` | 10s | Time in OPEN state before transitioning to HALF_OPEN |
| `permittedNumberOfCallsInHalfOpenState` | 2 | Test calls allowed in HALF_OPEN state |

The transition from OPEN to HALF_OPEN is automatic.

## Available Endpoints

With the application running at `http://localhost:8081`:

```
GET /actuator/health           → Overall health and circuit breaker status
GET /actuator/metrics          → Application metrics
GET /actuator/circuitbreakers  → Detailed circuit breaker status
```

## Run the Project

```bash
./mvnw spring-boot:run
```