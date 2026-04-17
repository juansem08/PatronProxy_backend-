package com.monitoring.proxy.service.impl;

import com.monitoring.proxy.model.LogEntry;
import com.monitoring.proxy.repository.LogRepository;
import com.monitoring.proxy.service.MicroserviceProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoggingProxy implements MicroserviceProxy<Object> {

    private final LogRepository logRepository;

    public Object execute(MicroserviceProxy<?> target, String serviceId, String operation, Object... params) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        String response = null;
        String stackTrace = null;

        try {
            Object result = target.execute(operation, params);
            response = result != null ? result.toString() : "null";
            return result;
        } catch (Exception e) {
            status = "ERROR";
            response = e.getMessage();
            stackTrace = Arrays.toString(e.getStackTrace());
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            LogEntry log = LogEntry.builder()
                    .requestId(requestId)
                    .serviceId(serviceId)
                    .operation(operation)
                    .durationMs(duration)
                    .status(status)
                    .timestamp(LocalDateTime.now())
                    .inputParams(Arrays.toString(params))
                    .response(response)
                    .stackTrace(stackTrace)
                    .build();
            
            logRepository.save(log);
        }
    }

    @Override
    public Object execute(String operation, Object... params) {
        // Generic execute if used directly (not common in this design)
        return null;
    }
}
