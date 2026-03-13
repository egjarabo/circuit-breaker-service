package com.egjarabo.circuitbreaker.service;

import com.egjarabo.circuitbreaker.exception.ExternalApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    private final AtomicReference<String> targetUrl =
            new AtomicReference<>("https://jsonplaceholder.typicode.com/todos/1");

    // URL válida y URL inválida
    private static final String VALID_URL   = "https://jsonplaceholder.typicode.com/todos/1";
    private static final String INVALID_URL = "https://api-inexistente-error.com/fail";

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Llama a la API externa protegida por el Circuit Breaker.
     *
     * @CircuitBreaker delega en Resilience4j para:
     *   - Contar fallos en la ventana deslizante (slidingWindowSize).
     *   - Abrir el circuito cuando se supera failureRateThreshold.
     *   - Invocar el fallback si el circuito está OPEN o la llamada falla.
     */
    @CircuitBreaker(name = "externalApiConfig", fallbackMethod = "fallbackResponse")
    public String callExternalApi() {
        try {
            return restTemplate.getForObject(targetUrl.get(), String.class);
        } catch (Exception e) {
            // Envolvemos en nuestra excepción de dominio para que el Circuit Breaker la contabilice
            throw new ExternalApiException("Fallo al conectar con la API externa", e);
        }
    }

    /**
     * Fallback invocado automáticamente por Resilience4j cuando la llamada falla
     * o el circuito está OPEN.
     *
     * Aquí puedes implementar cualquier estrategia de mitigación, por ejemplo:
     *   - Consultar una caché local
     *   - Devolver un valor por defecto
     *   - Llamar a un servicio alternativo
     */
    public String fallbackResponse(Throwable e) {
        return "El servicio externo no está disponible. Usando respuesta de emergencia.";
    }

    /**
     * Cambia la URL objetivo para simular un fallo o recuperación en tiempo real.
     * Este método es la herramienta didáctica que permite ver el cambio de estado.
     */
    public String switchUrl(boolean active) {
        if (active) {
            targetUrl.set(VALID_URL);
            return "URL restaurada a API real.";
        } else {
            targetUrl.set(INVALID_URL);
            return "URL cambiada a API inexistente.";
        }
    }

    public String getCurrentUrl() {
        return targetUrl.get();
    }
}
