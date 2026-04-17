package com.monitoring.proxy.repository;

import com.monitoring.proxy.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findTop20ByOrderByTimestampDesc();
    
    @Query("SELECT COUNT(l) FROM LogEntry l WHERE l.serviceId = :serviceId")
    long countByServiceId(String serviceId);

    @Query("SELECT COUNT(l) FROM LogEntry l WHERE l.serviceId = :serviceId AND l.status = 'ERROR'")
    long countErrorsByServiceId(String serviceId);

    @Query("SELECT AVG(l.durationMs) FROM LogEntry l WHERE l.serviceId = :serviceId")
    Double getAverageDurationByServiceId(String serviceId);
}
