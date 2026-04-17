package com.monitoring.proxy.controller;

import com.monitoring.proxy.model.LogEntry;
import com.monitoring.proxy.repository.LogRepository;
import com.monitoring.proxy.service.MicroserviceProxy;
import com.monitoring.proxy.service.impl.LoggingProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetricsController {

    private final LogRepository logRepository;
    private final LoggingProxy loggingProxy;

    @Qualifier("inventoryService")
    private final MicroserviceProxy<String> inventoryService;
    @Qualifier("orderService")
    private final MicroserviceProxy<String> orderService;
    @Qualifier("paymentService")
    private final MicroserviceProxy<String> paymentService;

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        String[] services = {"inventory", "orders", "payments"};
        
        long totalCalls = logRepository.count();
        long totalErrors = logRepository.findAll().stream().filter(l -> "ERROR".equals(l.getStatus())).count();
        double avgTime = logRepository.findAll().stream().mapToLong(LogEntry::getDurationMs).average().orElse(0);

        summary.put("totalCalls", totalCalls);
        summary.put("errorRate", totalCalls > 0 ? (double) totalErrors / totalCalls : 0);
        summary.put("avgResponseTime", avgTime);

        Map<String, Object> serviceStats = new HashMap<>();
        for (String s : services) {
            Map<String, Object> stats = new HashMap<>();
            long calls = logRepository.countByServiceId(s);
            long errors = logRepository.countErrorsByServiceId(s);
            Double avg = logRepository.getAverageDurationByServiceId(s);
            
            stats.put("calls", calls);
            stats.put("errorRate", calls > 0 ? (double) errors / calls : 0);
            stats.put("avgDuration", avg != null ? avg : 0);
            serviceStats.put(s, stats);
        }
        summary.put("services", serviceStats);
        
        return summary;
    }

    @GetMapping("/logs")
    public List<LogEntry> getLogs() {
        return logRepository.findTop20ByOrderByTimestampDesc();
    }

    @PostMapping("/simulate-load")
    public String simulateLoad() {
        Random random = new Random();
        String[] services = {"inventory", "orders", "payments"};
        String[] ops = {"create", "update", "delete", "query", "process"};

        for (int i = 0; i < 50; i++) {
            String service = services[random.nextInt(services.length)];
            String op = ops[random.nextInt(ops.length)];
            
            try {
                switch (service) {
                    case "inventory" -> loggingProxy.execute(inventoryService, service, op, "param" + i);
                    case "orders" -> loggingProxy.execute(orderService, service, op, "param" + i);
                    case "payments" -> loggingProxy.execute(paymentService, service, op, "param" + i);
                }
            } catch (Exception ignored) {
                // Ignore for simulation logs
            }
        }
        return "Simulation complete: 50 calls generated";
    }
}
