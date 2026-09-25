package com.andesstay.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String actor;

    @Column
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected AuditEvent() {
    }

    public AuditEvent(Long reservationId, String eventType, String actor, String details) {
        this.reservationId = reservationId;
        this.eventType = eventType;
        this.actor = actor;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getReservationId() { return reservationId; }
    public String getEventType() { return eventType; }
    public String getActor() { return actor; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
