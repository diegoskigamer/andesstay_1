package com.andesstay.controller;

import com.andesstay.dto.CreateTopicRequest;
import com.andesstay.service.KafkaAdminService;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/admin/kafka")
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    public KafkaAdminController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @GetMapping("/topics")
    public Collection<String> listTopics() throws ExecutionException, InterruptedException {
        return kafkaAdminService.listTopics();
    }

    @PostMapping("/topics/describe")
    public Map<String, TopicDescription> describe(@RequestBody List<String> names) throws ExecutionException, InterruptedException {
        return kafkaAdminService.describeTopics(names);
    }

    @PostMapping("/topics")
    public ResponseEntity<Void> createTopic(@RequestBody CreateTopicRequest request) throws ExecutionException, InterruptedException {
        kafkaAdminService.createTopic(request.getName(), request.getPartitions(), request.getReplicas());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/topics/{name}")
    public void deleteTopic(@PathVariable String name) throws ExecutionException, InterruptedException {
        kafkaAdminService.deleteTopic(name);
    }
}
