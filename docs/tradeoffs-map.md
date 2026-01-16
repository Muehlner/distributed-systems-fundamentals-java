# Trade-offs Map

## Latency vs Consistency
- Order creation returns immediately
- Final state is achieved asynchronously

## Availability vs Coordination
- No global locks
- Compensation instead of rollback

## Failure Handling vs Complexity
- Retries improve resilience
- Idempotency prevents duplication
- DLQs handle poison messages

## Observability vs Performance
- Full tracing enabled
- Metrics collected intentionally
