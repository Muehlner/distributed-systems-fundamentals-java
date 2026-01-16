CREATE TABLE IF NOT EXISTS orders (
                                      id            VARCHAR(64) PRIMARY KEY,
    status        VARCHAR(64) NOT NULL,
    customer_id   VARCHAR(128) NOT NULL,
    amount        DOUBLE PRECISION NOT NULL,
    currency      VARCHAR(16) NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
                                );

CREATE TABLE IF NOT EXISTS order_items (
                                           id        BIGSERIAL PRIMARY KEY,
                                           order_id  VARCHAR(64) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sku       VARCHAR(128) NOT NULL,
    qty       INTEGER NOT NULL
    );

CREATE TABLE IF NOT EXISTS processed_events (
                                                event_id      VARCHAR(128) PRIMARY KEY,
    processed_at  TIMESTAMP WITH TIME ZONE NOT NULL
                                );
