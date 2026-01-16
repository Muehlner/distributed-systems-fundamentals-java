CREATE TABLE IF NOT EXISTS inventory_reservations (
                                                      order_id       VARCHAR(64) PRIMARY KEY,
    status         VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    details        TEXT NOT NULL
    );

CREATE TABLE IF NOT EXISTS processed_events (
                                                event_id      VARCHAR(128) PRIMARY KEY,
    processed_at  TIMESTAMPTZ NOT NULL
    );
