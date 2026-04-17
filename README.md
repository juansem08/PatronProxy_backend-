# Microservices Monitoring - Backend (Spring Boot)

Este repositorio contiene la lógica del servidor para el sistema de monitoreo mediante el **Patrón Proxy**.

## Requisitos
*   Java 17+
*   Maven

## Características
*   Implementación de `LoggingProxy` para auditoría.
*   Simulación de 3 microservicios (Inventory, Orders, Payments).
*   Base de datos H2 integrada.
*   Endpoints de métricas y simulación de carga.

## Ejecución
```bash
mvn spring-boot:run
```
