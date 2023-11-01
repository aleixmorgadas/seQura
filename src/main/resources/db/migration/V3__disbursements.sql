CREATE TABLE IF NOT EXISTS disbursements
(
    reference VARCHAR(255)   NOT NULL PRIMARY KEY,
    merchant  VARCHAR(255)   NOT NULL,
    amount    NUMERIC(10, 2) NOT NULL,
    fees      NUMERIC(5, 2)  NOT NULL,
    date      DATE           NOT NULL,
    settled   BOOLEAN        NOT NULL
);

CREATE TABLE IF NOT EXISTS disbursements_orders
(
    id         SERIAL PRIMARY KEY,
    amount     NUMERIC(5, 2) NOT NULL,
    commission NUMERIC(5, 2) NOT NULL,
    created_at DATE          NOT NULL,
    reference  VARCHAR(255),
    FOREIGN KEY (reference) REFERENCES disbursements (reference)
);