# Architecture

This system follows an event-driven architecture designed to highlight
the challenges and trade-offs of distributed systems.

## Principles
- Explicit boundaries
- Independent data ownership
- Asynchronous communication
- Failure as a first-class concern

## Why No Distributed Transactions?
Distributed transactions increase coordination, reduce availability,
and amplify failures. This project intentionally avoids them to show
real-world alternatives.

## State Management
Each service owns its data and reacts to events to update its state.
