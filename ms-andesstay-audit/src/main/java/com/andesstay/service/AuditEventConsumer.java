package com.andesstay.service;

import com.andesstay.domain.AuditEvent;
import com.andesstay.dto.ReservationEvent;
import com.andesstay.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consume el topic "reservations.events" de Kafka y persiste cada evento
 * como una fila de auditoría. Kafka permite que este servicio (y
 * ms-andesstay-report) lean el mismo stream de eventos de forma
 * independiente, cada uno a su propio ritmo, sin que reservations tenga
 * que saber quién los consume.
 */
@Service
public class AuditEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final AuditEventRepository auditEventRepository;

    public AuditEventConsumer(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @KafkaListener(topics = "reservations.events", groupId = "ms-andesstay-audit")
    public void onReservationEvent(ReservationEvent event) {
        String details = "Cambio de estado: " + event.getPreviousStatus() + " -> " + event.getNewStatus();
        auditEventRepository.save(new AuditEvent(
                event.getReservationId(), event.getEventType(), event.getActor(), details
        ));
        log.info("Evento de auditoría guardado: reserva #{} - {}", event.getReservationId(), event.getEventType());
    }
}
