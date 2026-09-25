package com.andesstay.service;

import com.andesstay.config.RabbitConfig;
import com.andesstay.dto.ReservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consume los comandos que publica ms-andesstay-reservations en RabbitMQ.
 * Acá es donde en producción se integraría un proveedor real de email
 * (SES, SendGrid) o un sistema de tickets de housekeeping. Por ahora
 * queda como log, con el punto exacto marcado para conectar el proveedor
 * real más adelante.
 */
@Service
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void handleEmail(ReservationEvent event) {
        // TODO: integrar proveedor real de email (SES / SendGrid / SMTP)
        log.info("[EMAIL] Reserva #{} ({}) -> {} | evento: {}",
                event.getReservationId(), event.getGuestEmail(), event.getGuestName(), event.getEventType());
    }

    @RabbitListener(queues = RabbitConfig.HOUSEKEEPING_QUEUE)
    public void handleHousekeeping(ReservationEvent event) {
        // TODO: integrar sistema real de tickets de housekeeping
        log.info("[HOUSEKEEPING] Preparar unidad '{}' para reserva #{}",
                event.getUnitName(), event.getReservationId());
    }

    @RabbitListener(queues = RabbitConfig.VOUCHER_QUEUE)
    public void handleVoucher(ReservationEvent event) {
        // TODO: generar PDF/voucher real
        log.info("[VOUCHER] Generar voucher para reserva #{}", event.getReservationId());
    }
}
