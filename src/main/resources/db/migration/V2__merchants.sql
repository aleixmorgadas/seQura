CREATE TABLE IF NOT EXISTS merchants
(
    reference              VARCHAR(255) PRIMARY KEY,
    email                  VARCHAR(255) NOT NULL,
    live_on                VARCHAR(128) NOT NULL,
    disbursement_frequency VARCHAR(128) NOT NULL,
    minimum_monthly_fee    REAL         NOT NULL
);
