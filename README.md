# 🍪 Order Service — Event-Driven Architecture with Apache Kafka

A food delivery order management system inspired by iFood, built with **Java**, **Spring Boot**, and **Apache Kafka**. It simulates the full lifecycle of an order — from placement to delivery — using asynchronous event-driven communication.
 
---

## 📐 Architecture Flow

```
POST /orders/request
        │
        ▼
[topic-received-orders]
        │
        ▼
  PaymentListener ──── pendingOrders (ConcurrentHashMap)
                                │
                        POST /payments/confirm
                                │
                                ▼
                       [topic-approved-orders]
                                │
                        POST /orders/ship
                                │
                                ▼
                       [topic-delivering-orders]
                                │
                                ▼
                         StatusListener
                    (listens to all 3 topics)
```
 
---

## 🎨 Design Patterns

### Strategy
Each Kafka topic has a dedicated handler implementing the `HandleOrderEvent` interface:

```
HandleOrderEvent (interface)
    ├── HandleReceiveOrderEvent
    ├── HandleApprovedOrderEvent
    └── HandleDeliveringOrderEvent
```

Each handler encapsulates its own logic, keeping behavior isolated per event type.

### Factory
`HandleOrderEventFactory` receives the incoming topic name and returns the correct handler — no scattered if/else chains:

```java
public HandleOrderEvent getHandler(String topic) {
    return handlers.get(topic);
}
```

### Dependency Inversion Principle (DIP)
No `new` keyword for services or handlers. Spring manages all dependencies via constructor injection, keeping every component loosely coupled and easily testable.
 
---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Spring Boot | Application framework |
| Spring Kafka | Kafka producer & consumer |
| Apache Kafka | Event streaming |
| Kafka UI | Topic & consumer monitoring |
| Springdoc OpenAPI | Swagger UI / REST docs |
| Lombok | Boilerplate reduction |
| Docker | Kafka + Kafka UI containerization |
 
---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven
### 1. Clone the repository
```bash
git clone https://github.com/gustavoorafael/order-service.git
cd order-service
```

### 2. Configure the application
```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```
Edit `application.yaml` with your Kafka settings.

### 3. Start Kafka with Docker
```bash
docker-compose up -d
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

### 5. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 6. Access Kafka UI
```
http://localhost:8081
```
 
---

## 📡 API Endpoints

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/orders/request` | Place a new order → publishes to `topic-received-orders` |
| POST | `/orders/ship` | Ship an order → publishes to `topic-delivering-orders` |

**Request body:**
```json
{
  "id": "1",
  "product": "Chocolate Chip Cookie",
  "price": 5.50
}
```

### Payments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/payments/confirm` | Confirm payment → publishes to `topic-approved-orders` |

**Request body:**
```json
{
  "id": "1",
  "product": "Chocolate Chip Cookie",
  "price": 5.50
}
```
 
---

## 📊 Kafka Topics

| Topic | Trigger | Consumer |
|---|---|---|
| `topic-received-orders` | POST /orders/request | PaymentListener |
| `topic-approved-orders` | POST /payments/confirm | StatusListener |
| `topic-delivering-orders` | POST /orders/ship | StatusListener |
| `topic-*-retry` | Consumer failure | Retry mechanism |
| `topic-*-dlt` | Retry exhausted | Dead Letter Topic |
 
---

## 📋 Expected Log Output

```
Order successfully received: 1
Payment system: 5.50$ has to paid for the order 1
[Status #1]: Waiting payment!
[Status #1]: Order successfully paid! Preparing order!
Order successfully shipped: 1
[Status #1]: The order has been sent! Will be delivered soon!
```



