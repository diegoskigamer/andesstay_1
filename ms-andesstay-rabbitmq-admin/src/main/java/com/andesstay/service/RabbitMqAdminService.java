package com.andesstay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Envuelve la Management HTTP API de RabbitMQ (puerto 15672, viene
 * habilitada con el plugin rabbitmq_management que usamos en
 * infra/mq/compose.yml) para exponer un panel administrativo simplificado
 * y propio, en vez de dar acceso directo a la consola nativa de RabbitMQ.
 *
 * Referencia de la Management API: GET /api/queues, GET /api/overview,
 * DELETE /api/queues/{vhost}/{name}/contents (purgar).
 */
@Service
public class RabbitMqAdminService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rabbitmq.management-url}")
    private String managementUrl;

    @Value("${rabbitmq.management-username}")
    private String username;

    @Value("${rabbitmq.management-password}")
    private String password;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listQueues() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        return restTemplate.exchange(managementUrl + "/api/queues", HttpMethod.GET, request, List.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> overview() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        return restTemplate.exchange(managementUrl + "/api/overview", HttpMethod.GET, request, Map.class).getBody();
    }

    public void purgeQueue(String vhost, String queueName) {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        restTemplate.exchange(
                managementUrl + "/api/queues/" + vhost + "/" + queueName + "/contents",
                HttpMethod.DELETE, request, Void.class);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }
}
