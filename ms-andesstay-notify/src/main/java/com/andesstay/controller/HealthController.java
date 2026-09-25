package com.andesstay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Este servicio no expone una API pública de negocio (solo consume
 * RabbitMQ), pero sí un endpoint simple para verificar que está vivo.
 */
@RestController
public class HealthController {

    @GetMapping("/api/notify/status")
    public Map<String, String> status() {
        return Map.of("service", "ms-andesstay-notify", "status", "listening");
    }
}
