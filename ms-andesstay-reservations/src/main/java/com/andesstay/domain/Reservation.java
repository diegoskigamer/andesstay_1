package com.andesstay.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A diferencia del monolito original, acá NO guardamos la entidad Unit
 * completa (esa vive en ms-andesstay-catalog, otra base de datos). Solo
 * guardamos el unitId y una copia liviana de los datos que necesitamos
 * mostrar (nombre, hostal), traídos en el momento de crear la reserva
 * vía CatalogClient.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String unitName;

    @Column(nullable = false)
    private String unitHostelName;

    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String guestEmail;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Reservation() {
    }

    public Reservation(Long unitId, String unitName, String unitHostelName, String guestName,
                        String guestEmail, LocalDate checkInDate, LocalDate checkOutDate, String createdBy) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.unitHostelName = unitHostelName;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.CREADA;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUnitId() { return unitId; }
    public String getUnitName() { return unitName; }
    public String getUnitHostelName() { return unitHostelName; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
