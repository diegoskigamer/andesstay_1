package com.andesstay.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Copia local (propia de este microservicio) de los eventos de reserva,
 * alimentada por Kafka. report-svc NUNCA consulta la base de datos de
 * reservations directamente — solo lee lo que le llega por streaming,
 * que es justo la idea de tener KPIs desacoplados del sistema
 * transaccional.
 */
@Entity
@Table(name = "reservation_event_records")
public class ReservationEventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String unitName;

    @Column(nullable = false)
    private String eventType;

    @Column
    private String newStatus;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    protected ReservationEventRecord() {
    }

    public ReservationEventRecord(Long reservationId, Long unitId, String unitName,
                                   String eventType, String newStatus, LocalDateTime eventTimestamp) {
        this.reservationId = reservationId;
        this.unitId = unitId;
        this.unitName = unitName;
        this.eventType = eventType;
        this.newStatus = newStatus;
        this.eventTimestamp = eventTimestamp;
        this.receivedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getReservationId() { return reservationId; }
    public Long getUnitId() { return unitId; }
    public String getUnitName() { return unitName; }
    public String getEventType() { return eventType; }
    public String getNewStatus() { return newStatus; }
    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
}
