package com.andesstay.dto;

import java.math.BigDecimal;

/** Respuesta que devuelve ms-andesstay-catalog al consultar una unidad. */
public class UnitDto {
    private Long id;
    private String name;
    private String hostelName;
    private Integer availableCount;
    private Integer totalCount;
    private BigDecimal nightlyRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
    public Integer getAvailableCount() { return availableCount; }
    public void setAvailableCount(Integer availableCount) { this.availableCount = availableCount; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public void setNightlyRate(BigDecimal nightlyRate) { this.nightlyRate = nightlyRate; }
}
