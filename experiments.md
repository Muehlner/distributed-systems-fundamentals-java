# Experiments

## Experiment 1 — Eventual Consistency
1. Create an order
2. Observe initial status
3. Poll until final state

## Experiment 2 — Partial Failure
1. Configure inventory-service to fail
2. Create an order
3. Observe refund compensation

## Experiment 3 — Retries and Duplication
1. Increase retry attempts
2. Force consumer failures
3. Validate idempotent behavior

## Experiment 4 — Tracing
1. Create an order with correlation-id
2. Open Jaeger
3. Follow the request across services
