package com.andesstay.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Envuelve el AdminClient de Kafka para exponer operaciones básicas de
 * administración (listar topics, ver detalle, crear) vía REST, en vez de
 * requerir acceso directo a la línea de comandos de Kafka.
 */
@Service
public class KafkaAdminService {

    private final AdminClient adminClient;

    public KafkaAdminService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public Collection<String> listTopics() throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get();
    }

    public Map<String, TopicDescription> describeTopics(List<String> names) throws ExecutionException, InterruptedException {
        return adminClient.describeTopics(names).allTopicNames().get();
    }

    public void createTopic(String name, int partitions, short replicas) throws ExecutionException, InterruptedException {
        adminClient.createTopics(List.of(new NewTopic(name, partitions, replicas))).all().get();
    }

    public void deleteTopic(String name) throws ExecutionException, InterruptedException {
        adminClient.deleteTopics(List.of(name)).all().get();
    }
}
