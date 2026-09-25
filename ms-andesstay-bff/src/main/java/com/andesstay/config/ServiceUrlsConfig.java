package com.andesstay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
public class ServiceUrlsConfig {

    @Value("${services.reservations-url}")
    private String reservationsUrl;

    @Value("${services.catalog-url}")
    private String catalogUrl;

    @Value("${services.audit-url}")
    private String auditUrl;

    @Value("${services.report-url}")
    private String reportUrl;

    public String getReservationsUrl() { return reservationsUrl; }
    public String getCatalogUrl() { return catalogUrl; }
    public String getAuditUrl() { return auditUrl; }
    public String getReportUrl() { return reportUrl; }
}
