# Microservice Application

This is a modular and scalable microservice-based application built using **Java**, **Spring Boot**, **Thymeleaf**, and **Apache Kafka**. The application is containerized using **Docker** and supports deployment via **Docker Compose** or **Kubernetes**. Kafka is used for asynchronous communication between microservices.

## 🎯 Purpose

The primary goal of this application is to support **arbitrage trading** by:

- Collecting and analyzing real-time market data.

- Identifying profitable buy-sell pairs.

- Presenting arbitrage opportunities to users through a clean and interactive web interface.


The system continuously monitors the marketplace and uses filters, thresholds to compute and display potential arbitrage trades that can yield profit.

---

## 📦 Project Structure

### `deployment/`

Contains the deployment configurations for running the services and their dependencies.

---

### Services

Includes the source code for each microservice. Each service is self-contained with its own configuration and dependencies.

* **`profitable-orders-service/`**
  Processes and manages profitable orders. Works with `order-analyzer-service` and Kafka for real-time data processing.

* **`web-ui-service/`**
  Provides a web-based user interface using Thymeleaf. Acts as the frontend for the entire application.

* **`order-analyzer-service/`**
  Analyzes order data and generates insights. Integrates with Kafka to consume and produce event-driven messages.

* **`market-api-reader/`**
  Fetches market data from external APIs and interacts with `static-data-service` for reference data.

* **`static-data-service/`**
  Maintains static reference data and exposes REST APIs for other services to consume.

---

## 🚀 Technologies Used

* Java 17+
* Spring Boot
* Thymeleaf
* Apache Kafka
* Docker, Docker Compose
* Kubernetes (optional)
* PostgreSQL (or any RDBMS used)

---

## 🧪 How to Run

> Ensure Docker and Docker Compose are installed on your system.

**To run the application:**

```bash
docker-compose -f docker-compose.yml up -d --build
```
