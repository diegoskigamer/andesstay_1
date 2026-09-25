package com.andesstay.controller;

import com.andesstay.config.ServiceUrlsConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * El BFF no reimplementa la lógica de negocio — solo valida el JWT
 * (SecurityConfig) y reenvía cada request al microservicio de dominio
 * correspondiente, preservando método, headers, body y query string.
 * Así el frontend le sigue hablando a UNA sola URL base (el BFF) sin
 * saber que por detrás hay 5 servicios distintos.
 */
@RestController
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ServiceUrlsConfig serviceUrls;

    public ProxyController(ServiceUrlsConfig serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    @RequestMapping("/api/reservations/**")
    public ResponseEntity<Object> reservations() {
        return forward(serviceUrls.getReservationsUrl());
    }

    @RequestMapping("/api/catalog/**")
    public ResponseEntity<Object> catalog() {
        return forward(serviceUrls.getCatalogUrl());
    }

    @RequestMapping("/api/audit/**")
    public ResponseEntity<Object> audit() {
        return forward(serviceUrls.getAuditUrl());
    }

    @RequestMapping("/api/report/**")
    public ResponseEntity<Object> report() {
        return forward(serviceUrls.getReportUrl());
    }

    private ResponseEntity<Object> forward(String targetBaseUrl) {
        HttpServletRequest original = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String path = original.getRequestURI();
        String query = original.getQueryString();
        String targetUrl = targetBaseUrl + path + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = original.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if ("host".equalsIgnoreCase(name) || "content-length".equalsIgnoreCase(name)) continue;
            headers.add(name, original.getHeader(name));
        }

        byte[] body = readBody(original);
        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
        HttpMethod method = HttpMethod.valueOf(original.getMethod());

        return restTemplate.exchange(targetUrl, method, entity, Object.class);
    }

    private byte[] readBody(HttpServletRequest request) {
        try {
            return request.getInputStream().readAllBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
