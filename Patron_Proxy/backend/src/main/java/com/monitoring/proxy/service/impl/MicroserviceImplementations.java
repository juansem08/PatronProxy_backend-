package com.monitoring.proxy.service.impl;

import com.monitoring.proxy.service.MicroserviceProxy;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service("inventoryService")
class InventoryService implements MicroserviceProxy<String> {
    @Override
    public String execute(String operation, Object... params) {
        return "Inventory " + operation + " executed successfully";
    }
}

@Service("orderService")
class OrderService implements MicroserviceProxy<String> {
    @Override
    public String execute(String operation, Object... params) {
        return "Order " + operation + " processed";
    }
}

@Service("paymentService")
class PaymentService implements MicroserviceProxy<String> {
    private final Random random = new Random();

    @Override
    public String execute(String operation, Object... params) {
        // Simular fallos aleatorios: 10% de las llamadas fallan
        if (random.nextDouble() < 0.10) {
            throw new RuntimeException("Payment processing failed: insufficient funds");
        }
        return "Payment " + operation + " confirmed";
    }
}
