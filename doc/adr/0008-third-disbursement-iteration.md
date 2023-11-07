# 8. third disbursement iteration

Date: 2023-11-07

## Status

Accepted

## Context

I experimented with making the Disbursements a view but indeed, it wasn't as good as I initially expected.

It complicated the calculation logic for the aggregations.

## Decision

Make the disbursements a domain entity by creating a reference of `Disbursements Orders` based on a deterministic `reference` as Id.

The way to ensure that we can write `DisbursementOrders` without requiring a `Disbursement` entity is by creating a deterministic reference based on:

- `Order::createdAt`
- `Merchant::frequency` and `Merchant::reference`

The process to determine the `DisbursementReference` in a deterministic way is:

- If disbursement frequency == `DAILY`:
  - DisbursementReference = `<merchant_reference>-<order_created_at_plus_one_day>`
- If disbursement frequency == `WEEKLY`:
  - DisbursementReference = `<merchant_reference>-<based_on_merchant_live_on_next_weekday>`

Then, when we perform the disbursement for a day, we just select all orders for each merchant with 
the reference `<merchant_reference>-<disbursment_date>`.

## Consequences

We might encounter that modifying an existing `Disbursement` to be more complicated but due to
audit purposes, changing that data shouldn't be allowed as it is part of the accounting data.