package com.monitoring.proxy.service;

public interface MicroserviceProxy<T> {
    T execute(String operation, Object... params);
}
