CREATE TABLE IF NOT EXISTS disbursements
(
    reference VARCHAR(255)   NOT NULL PRIMARY KEY,
    merchant  VARCHAR(255)   NOT NULL,
    amount    NUMERIC(10, 2) NOT NULL,
    fees      NUMERIC(10, 2) NOT NULL,
    date      DATE           NOT NULL,
    version   bigint         NOT NULL
);

CREATE TABLE IF NOT EXISTS disbursements_orders
(
    id         SERIAL PRIMARY KEY,
    merchant   VARCHAR(255)   NOT NULL,
    amount     NUMERIC(10, 2) NOT NULL,
    commission NUMERIC(10, 2) NOT NULL,
    created_at DATE           NOT NULL,
    reference  VARCHAR(255)   NOT NULL
);

ALTER TABLE disbursements_orders
    ADD CONSTRAINT disbursements_orders_id_reference UNIQUE (id, reference);