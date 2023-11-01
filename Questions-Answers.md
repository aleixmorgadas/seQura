# Questions-Answers to clarity doubts on [Challenge](./CHALLENGE.md)

    On the requirements, we specify the strictness of operations of less than `50 EUR` but no in the relationship of `300 EUR`.

    Do we charge a fee of `0.95%` when it is strictly less than `300 EUR` (<) or `300 EUR` or less (<=)?

300 EUR or less (<=)

> In the `orders.csv` file, it doesn’t contain any transaction id. It means that, if we find two orders with for the same merchant, same amount, and same date, we need to consider them different orders as we cannot distinguish between them
We consider them as different orders.

    When a `disbursement` for a day happened with the others already settled. If we upload a new `orders.csv` file that contain orders for dates and merchants that already have been disbursed. How do we proceed?

    Do we assume new orders will not happen on past events?
    Do we consider them new orders, and we create a new `disbursement` for that date with the new orders?

In new orders.csv we assume the orders will not happen on past events.
We create a new disbursement for that date with the new orders.

    We can only disburse the orders from previous day as the data doesn’t container the hour of the order.

    Which it will make it hard to know if we need to place the order in the same day.
    Also, payment systems might include some variability on the transaction confirmation behavior. So, including transactions before the 8:00 AM might cause problems as we can have transactions authorized but not confirmed.
    It means that a `disbursement` contains the orders from the previous day in case for the daily distributed merchants, and last 7 days (but not the actual) for the weekly distributed merchants.
    Is this correct?

The understanding is correct. The disbursement should include orders from previous day/s.