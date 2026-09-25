package com.andesstay.messaging;

import com.andesstay.config.KafkaTopicConfig;
import com.andesstay.config.RabbitConfig;
import com.andesstay.dto.ReservationEvent;
import com.andesstay.domain.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Punto único donde ReservationService dispara los dos tipos de mensajería:
 * RabbitMQ (comandos puntuales para notify-svc) y Kafka (streaming de
 * eventos para audit-svc y report-svc). Ver ReservationEvent para el
 * detalle de por qué se usan ambos.
 */
@Component
public class ReservationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate,
                                      KafkaTemplate<String, ReservationEvent> kafkaTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(ReservationEvent event) {
        publishToKafka(event);
        sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
    }

    public void publishStatusChanged(ReservationEvent event) {
        publishToKafka(event);

        ReservationStatus newStatus = event.getNewStatus();
        if (newStatus == ReservationStatus.CONFIRMADA) {
            sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
        } else if (newStatus == ReservationStatus.CHECKIN_PENDIENTE) {
            sendCommand(RabbitConfig.CMD_TOPIC_EXCHANGE, "housekeeping.checkin", event);
        } else if (newStatus == ReservationStatus.CHECKOUT || newStatus == ReservationStatus.CANCELADA) {
            sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
        }
    }

    private void publishToKafka(ReservationEvent event) {
        try {
            kafkaTemplate.send(KafkaTopicConfig.RESERVATIONS_EVENTS_TOPIC,
                    String.valueOf(event.getReservationId()), event);
        } catch (Exception e) {
            // No bloqueamos la operación de negocio si Kafka no está disponible
            // (por ejemplo, en desarrollo local sin el clúster corriendo).
            log.warn("No se pudo publicar en Kafka (¿está corriendo el clúster?): {}", e.getMessage());
        }
    }

    private void sendCommand(String exchange, String routingKey, ReservationEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            log.warn("No se pudo publicar en RabbitMQ (¿está corriendo el broker?): {}", e.getMessage());
        }
    }
}
