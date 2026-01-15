# \# 🧠 Distributed Systems Trade-offs Lab

# 

# > A hands-on laboratory to understand \*\*why distributed systems are hard\*\* — and how architectural trade-offs shape real systems.

# 

# ---

# 

# \## 📌 Overview

# 

# This repository is a \*\*practical reference project\*\* designed to demonstrate the \*\*fundamental trade-offs of distributed systems\*\*.

# 

# Instead of presenting an idealized or “perfect” architecture, this project intentionally exposes:

# \- Latency

# \- Partial failures

# \- Eventual consistency

# \- Coordination costs

# \- Retry and duplication problems

# \- Observability challenges

# 

# The goal is to make distributed systems \*\*concrete, observable, and understandable\*\*.

# 

# This repository is the practical companion to the article:

# 

# > \*\*Why Distributed Systems Are Hard: The Core Trade-offs\*\*

# 

# ---

# 

# \## 🎯 Purpose

# 

# Distributed systems are difficult not because engineers lack skill,  

# but because \*\*distribution introduces unavoidable limits\*\*.

# 

# This project exists to:

# \- Connect theory with real system behavior

# \- Make architectural trade-offs explicit

# \- Provide a reproducible learning environment

# \- Serve as a portfolio-ready example of systems thinking

# 

# Complexity here is \*\*intentional\*\*, not accidental.

# 

# ---

# 

# \## ❓ What This Project Is (and Is Not)

# 

# \### ✅ This project \*\*is\*\*

# \- A learning-oriented distributed system

# \- A reference lab for architectural trade-offs

# \- A reproducible environment with controlled failures

# \- A practical complement to theoretical articles

# \- A portfolio-ready example for architects and senior engineers

# 

# \### ❌ This project \*\*is not\*\*

# \- A production-ready system

# \- A framework or template

# \- An example of “best practices only”

# \- Optimized for simplicity or minimal code

# 

# ---

# 

# \## 🧱 Architecture Overview

# 

# \### Services

# \- \*\*order-service\*\*  

# &nbsp; Orchestrates the order lifecycle (SAGA-like coordinator)

# 

# \- \*\*payment-service\*\*  

# &nbsp; Simulates payment authorization and refunds, including failures and retries

# 

# \- \*\*inventory-service\*\*  

# &nbsp; Simulates inventory reservation with partial failures

# 

# \### Infrastructure

# \- PostgreSQL (one database per service)

# \- Kafka (event-driven communication)

# \- OpenTelemetry + Jaeger (distributed tracing)

# \- Prometheus + Grafana (metrics and monitoring)

# 

# \### Architectural Style

# \- Event-driven

# \- Eventually consistent

# \- No distributed transactions (no 2PC)

# \- Compensation-based failure handling

# 

# ---

# 

# \## 🔄 High-level Flow

# 

# 1\. A client creates an order via `order-service`

# 2\. `order-service` emits an \*\*OrderCreated\*\* event

# 3\. `payment-service` authorizes payment and emits a result event

# 4\. `inventory-service` reserves stock and emits a result event

# 5\. `order-service` updates the final order state

# 6\. If a failure occurs after payment, a \*\*refund compensation\*\* is triggered

# 

# ---

# 

# \## 🧠 Trade-offs Demonstrated

# 

# | Trade-off | Where it appears |

# |----------|------------------|

# | Network vs Local Calls | Inter-service communication |

# | Latency vs Consistency | Fast order creation vs delayed final state |

# | Availability vs Coordination | No global locks or distributed transactions |

# | Failure Handling vs Complexity | Retries, idempotency, DLQs |

# | Observability vs Performance | Tracing, metrics, logging |

# 

# ---

# 

# \## 🚀 How to Run Locally

# 

# \### Prerequisites

# \- Docker

# \- Docker Compose

# 

# \### Start infrastructure

# ```bash

# docker compose up -d

---

## 🧭 Runtime Information

### Available UIs
- **Jaeger (Distributed Tracing):** http://localhost:16686
- **Prometheus (Metrics):** http://localhost:9090
- **Grafana (Dashboards):** http://localhost:3000  
  - user: `admin`  
  - password: `admin`

---

## 📡 Service Ports

| Service | Port |
|------|------|
| order-service | 8080 |
| payment-service | 8081 |
| inventory-service | 8082 |
| Kafka (host access) | 9094 |

---

## 🔍 Observability

### Distributed Tracing
A **correlation ID** is propagated across all service boundaries via:
- HTTP header: `X-Correlation-Id`
- Kafka event envelope

All services export traces using **OpenTelemetry**.

With Jaeger, you can:
- Follow a single order across multiple services
- Observe latency accumulation across hops
- Identify failure propagation and retries

### Metrics
The system exposes metrics for:
- HTTP latency and error rates
- Kafka producer and consumer behavior
- Business-level events (orders, payments, failures)

---

## 🧪 Experiments

This system is intentionally designed to be **broken on purpose**.

See [`docs/experiments.md`](docs/experiments.md) for step-by-step experiments, including:
- Eventual consistency behavior
- Partial failure scenarios
- Retry and duplication handling
- Compensation flows
- Distributed tracing analysis

---

## 📚 Documentation

- [`docs/architecture.md`](docs/architecture.md) — architecture and design principles  
- [`docs/tradeoffs-map.md`](docs/tradeoffs-map.md) — trade-off → component mapping  
- [`docs/experiments.md`](docs/experiments.md) — reproducible experiments  

---

## 🧠 Key Takeaway

> Distributed systems are not hard because engineers are bad.  
> They are hard because **distribution introduces unavoidable trade-offs**.

This repository exists to make those trade-offs **visible, testable, and understandable**.



