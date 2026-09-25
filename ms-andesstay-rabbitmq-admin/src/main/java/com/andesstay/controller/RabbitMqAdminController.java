package com.andesstay.controller;

import com.andesstay.service.RabbitMqAdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API administrativa simplificada para RabbitMQ. Pensado para uso
 * exclusivo de Admin (protegido a nivel de API Gateway/BFF en el
 * despliegue final, ya que este servicio en sí no valida JWT — vive
 * dentro de la red interna de ec2-apps, no expuesto a Internet).
 */
@RestController
@RequestMapping("/api/admin/rabbitmq")
public class RabbitMqAdminController {

    private final RabbitMqAdminService adminService;

    public RabbitMqAdminController(RabbitMqAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return adminService.overview();
    }

    @GetMapping("/queues")
    public List<Map<String, Object>> queues() {
        return adminService.listQueues();
    }

    @DeleteMapping("/queues/{vhost}/{name}/purge")
    public void purgeQueue(@PathVariable String vhost, @PathVariable String name) {
        adminService.purgeQueue(vhost, name);
    }
}
