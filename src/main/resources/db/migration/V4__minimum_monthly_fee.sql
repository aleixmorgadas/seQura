CREATE TABLE IF NOT EXISTS minimum_monthly_fee
(
    reference VARCHAR(140) NOT NULL PRIMARY KEY,
    amount    NUMERIC(10, 2),
    month     DATE         NOT NULL
)