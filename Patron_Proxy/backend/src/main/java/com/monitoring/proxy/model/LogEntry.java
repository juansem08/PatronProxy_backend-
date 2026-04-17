package com.monitoring.proxy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId;
    private String serviceId;
    private String operation;
    private Long durationMs;
    private String status; // SUCCESS / ERROR
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String inputParams;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;
}
