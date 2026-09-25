package com.andesstay.dto;

import com.andesstay.domain.ReservationStatus;
import java.time.LocalDateTime;

/**
 * Evento de dominio publicado cada vez que se crea una reserva o cambia
 * de estado. Se manda por DOS canales distintos, con propósitos distintos:
 *
 * - RabbitMQ (cola de comandos): para que ms-andesstay-notify dispare
 *   acciones puntuales (mandar un email, crear un ticket de housekeeping).
 *   Es mensajería de "hazlo una vez, ahora".
 *
 * - Kafka (topic de streaming): para que ms-andesstay-audit y
 *   ms-andesstay-report consuman el mismo evento como un log histórico,
 *   permanente, que pueden releer o reprocesar cuando quieran.
 */
public class ReservationEvent {
    private Long reservationId;
    private Long unitId;
    private String unitName;
    private String guestName;
    private String guestEmail;
    private String eventType; // RESERVA_CREADA, ESTADO_CONFIRMADA, etc.
    private ReservationStatus previousStatus;
    private ReservationStatus newStatus;
    private String actor;
    private LocalDateTime timestamp;

    public ReservationEvent() {
    }

    public ReservationEvent(Long reservationId, Long unitId, String unitName, String guestName,
                             String guestEmail, String eventType, ReservationStatus previousStatus,
                             ReservationStatus newStatus, String actor) {
        this.reservationId = reservationId;
        this.unitId = unitId;
        this.unitName = unitName;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.actor = actor;
        this.timestamp = LocalDateTime.now();
    }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public ReservationStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ReservationStatus previousStatus) { this.previousStatus = previousStatus; }
    public ReservationStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ReservationStatus newStatus) { this.newStatus = newStatus; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
