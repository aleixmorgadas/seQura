CREATE TABLE IF NOT EXISTS orders
(
    id                 SERIAL PRIMARY KEY,
    merchant_reference VARCHAR(255) NOT NULL,
    amount             VARCHAR(128) NOT NULL,
    created_at         DATE         NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS orders_seq
    increment 1
    start 1;