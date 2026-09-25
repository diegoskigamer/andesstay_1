package com.andesstay.service;

import com.andesstay.domain.ReservationEventRecord;
import com.andesstay.dto.ReservationEvent;
import com.andesstay.repository.ReservationEventRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventConsumer.class);

    private final ReservationEventRecordRepository repository;

    public ReservationEventConsumer(ReservationEventRecordRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "reservations.events", groupId = "ms-andesstay-report")
    public void onReservationEvent(ReservationEvent event) {
        repository.save(new ReservationEventRecord(
                event.getReservationId(), event.getUnitId(), event.getUnitName(),
                event.getEventType(), event.getNewStatus(), event.getTimestamp()
        ));
        log.info("Evento registrado para KPIs: reserva #{} - {}", event.getReservationId(), event.getEventType());
    }
}
