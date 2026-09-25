package com.andesstay.repository;

import com.andesstay.domain.ReservationEventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationEventRecordRepository extends JpaRepository<ReservationEventRecord, Long> {
    List<ReservationEventRecord> findByEventTimestampAfter(LocalDateTime after);
}
