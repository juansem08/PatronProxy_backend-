package com.monitoring.proxy.controller;

import com.monitoring.proxy.service.MicroserviceProxy;
import com.monitoring.proxy.service.impl.LoggingProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceController {

    private final LoggingProxy loggingProxy;
    
    @Qualifier("inventoryService")
    private final MicroserviceProxy<String> inventoryService;
    
    @Qualifier("orderService")
    private final MicroserviceProxy<String> orderService;
    
    @Qualifier("paymentService")
    private final MicroserviceProxy<String> paymentService;

    @PostMapping("/{service}/{operation}")
    public Object callService(@PathVariable String service, @PathVariable String operation, @RequestBody(required = false) Map<String, Object> params) {
        Object[] paramsArray = params != null ? params.values().toArray() : new Object[0];
        
        return switch (service.toLowerCase()) {
            case "inventory" -> loggingProxy.execute(inventoryService, "inventory", operation, paramsArray);
            case "orders" -> loggingProxy.execute(orderService, "orders", operation, paramsArray);
            case "payments" -> loggingProxy.execute(paymentService, "payments", operation, paramsArray);
            default -> throw new IllegalArgumentException("Unknown service: " + service);
        };
    }
}
