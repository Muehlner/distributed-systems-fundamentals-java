CREATE TABLE IF NOT EXISTS payments (
                                        order_id       VARCHAR(64) PRIMARY KEY,
    status         VARCHAR(32) NOT NULL,
    amount         DOUBLE PRECISION NOT NULL,
    currency       VARCHAR(16) NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    updated_at     TIMESTAMPTZ NOT NULL,
    details        TEXT NOT NULL
    );

CREATE TABLE IF NOT EXISTS processed_events (
                                                event_id      VARCHAR(128) PRIMARY KEY,
    processed_at  TIMESTAMPTZ NOT NULL
    );
