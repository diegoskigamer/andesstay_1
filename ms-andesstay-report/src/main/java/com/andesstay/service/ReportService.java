package com.andesstay.service;

import com.andesstay.domain.ReservationEventRecord;
import com.andesstay.repository.ReservationEventRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReservationEventRecordRepository repository;

    public ReportService(ReservationEventRecordRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> kpis(String range) {
        int hours = parseRangeToHours(range);
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<ReservationEventRecord> recent = repository.findByEventTimestampAfter(since);

        long created = recent.stream().filter(r -> "RESERVA_CREADA".equals(r.getEventType())).count();
        long checkouts = recent.stream().filter(r -> "CHECKOUT".equals(r.getNewStatus())).count();
        long activeStays = recent.stream().filter(r -> "EN_ESTADIA".equals(r.getNewStatus())).count();

        Map<String, Long> byHour = recent.stream()
                .filter(r -> "RESERVA_CREADA".equals(r.getEventType()))
                .collect(Collectors.groupingBy(
                        r -> r.getEventTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00")),
                        TreeMap::new, Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("range", range);
        result.put("totalReservations", created);
        result.put("reservationsByHour", byHour);
        result.put("checkoutsCompleted", checkouts);
        result.put("activeStays", activeStays);
        return result;
    }

    public List<Map<String, Object>> topUnits(String range) {
        int hours = parseRangeToHours(range);
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<ReservationEventRecord> recent = repository.findByEventTimestampAfter(since);

        Map<String, Long> byUnit = recent.stream()
                .filter(r -> "RESERVA_CREADA".equals(r.getEventType()))
                .collect(Collectors.groupingBy(ReservationEventRecord::getUnitName, Collectors.counting()));

        return byUnit.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("unitName", e.getKey());
                    m.put("reservationsCount", e.getValue());
                    return m;
                })
                .toList();
    }

    private int parseRangeToHours(String range) {
        if (range == null || range.isBlank()) return 24;
        return switch (range) {
            case "last24h" -> 24;
            case "last7d" -> 24 * 7;
            case "last30d" -> 24 * 30;
            default -> 24;
        };
    }
}
