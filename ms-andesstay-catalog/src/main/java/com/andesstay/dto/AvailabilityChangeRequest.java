package com.andesstay.dto;

/**
 * Usado internamente por ms-andesstay-reservations (vía CatalogClient) para
 * pedirle a este servicio que baje o suba la disponibilidad de una unidad,
 * sin necesitar acceso directo a la base de datos de catálogo.
 */
public class AvailabilityChangeRequest {
    private String direction; // "DECREASE" | "INCREASE"

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
