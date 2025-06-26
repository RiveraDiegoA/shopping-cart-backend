# 🛒 Carrito Digital - Backend (Java + WebFlux + MongoDB)

Sistema de carrito de compras con arquitectura Clean Architecture, desarrollado en **Java 17**, **Spring Boot WebFlux**, **MongoDB** y **Docker**.

## 🚀 Microservicios

| Nombre             | Puerto |
|--------------------|--------|
| categoria-service  | 8080   |
| moneda-service     | 8081   |
| producto-service   | 8082   |
| cupon-service      | 8083   |
| carrito-service    | 8084   |

MongoDB se expone en el puerto 27017.

---

## 🧩 Pre-requisitos

- Docker
- Docker Compose
- Java 17+ (para compilación con Gradle/Maven)

---

## ❗ ¿Cómo levantar todo?

1. Clona el repositorio
2. Compila los servicios:
   ```bash
   cd categoria-service && ./gradlew build
   cd ../moneda-service && ./gradlew build
   cd ../producto-service && ./gradlew build
   cd ../cupon-service && ./gradlew build
   cd ../carrito-service && ./gradlew build
