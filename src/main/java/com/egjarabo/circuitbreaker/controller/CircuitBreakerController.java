package com.egjarabo.circuitbreaker.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class CircuitBreakerController {

    private final RestTemplate restTemplate = new RestTemplate();

    // URL inicial correcta
    private String targetUrl = "https://jsonplaceholder.typicode.com/todos/1";

    @GetMapping("/check")
    @CircuitBreaker(name = "externalApiConfig", fallbackMethod = "fallbackResponse")
    public String callExternalApi() {
        return restTemplate.getForObject(targetUrl, String.class);
    }

    // Método para cambiar la URL en tiempo de ejecución
    @GetMapping("/switch")
    public String switchUrl(@RequestParam boolean active) {
        if (active) {
            this.targetUrl = "https://jsonplaceholder.typicode.com/todos/1";
            return "URL restaurada a API Real.";
        } else {
            this.targetUrl = "https://api-inexistente-error.com/fail";
            return "URL cambiada a API Inexistente (Simulando caída).";
        }
    }

    // El Fallback
    public String fallbackResponse(Throwable e) {
        return "FALLBACK: La API externa no responde. Usando datos locales de emergencia.";
    }
}
