package com.egjarabo.circuitbreaker.controller;

import com.egjarabo.circuitbreaker.service.ExternalApiService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CircuitBreakerController {

    private final ExternalApiService externalApiService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerController(ExternalApiService externalApiService,
                                    CircuitBreakerRegistry circuitBreakerRegistry) {
        this.externalApiService = externalApiService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * Endpoint principal de la demo.
     * Llama a la API externa a través del servicio (con CB activo).
     * Llamar varias veces con URL inválida abrirá el circuito.
     */
    @GetMapping("/check")
    public String check() {
        return externalApiService.callExternalApi();
    }

    /**
     * Permite simular fallos o recuperación en tiempo real.
     *
     * ?active=false → apunta a URL inexistente (simula caída del servicio externo)
     * ?active=true  → restaura URL válida (simula recuperación)
     */
    @GetMapping("/switch")
    public String switchUrl(@RequestParam boolean active) {
        return externalApiService.switchUrl(active);
    }

    /**
     * Muestra el estado actual del Circuit Breaker y sus métricas.
     * Útil para la demo sin necesidad de abrir el dashboard.
     *
     * Estados posibles: CLOSED | OPEN | HALF_OPEN | DISABLED | FORCED_OPEN
     */
    @GetMapping("/status")
    public String status() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("externalApiConfig");
        CircuitBreaker.Metrics metrics = cb.getMetrics();

        return String.format(
                "Estado: %s | URL actual: %s | Tasa de fallos: %.1f%% | " +
                        "Llamadas en buffer: %d | Llamadas no permitidas: %d",
                cb.getState(),
                externalApiService.getCurrentUrl(),
                metrics.getFailureRate(),
                metrics.getNumberOfBufferedCalls(),
                metrics.getNumberOfNotPermittedCalls()
        );
    }
}
