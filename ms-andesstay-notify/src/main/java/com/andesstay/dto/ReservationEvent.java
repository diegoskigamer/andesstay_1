package com.andesstay.dto;

import java.time.LocalDateTime;

/**
 * Copia del evento que publica ms-andesstay-reservations. Cada
 * microservicio mantiene su propia copia del "contrato" del evento (no
 * comparten código/librería) — es una decisión intencional en
 * arquitecturas de microservicios para no acoplar los servicios entre sí
 * a nivel de compilación.
 */
public class ReservationEvent {
    private Long reservationId;
    private Long unitId;
    private String unitName;
    private String guestName;
    private String guestEmail;
    private String eventType;
    private String previousStatus;
    private String newStatus;
    private String actor;
    private LocalDateTime timestamp;

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
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
