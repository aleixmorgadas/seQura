# seQura backend coding challenge

This coding challenge is for people who applied to the Senior Backend Developer position at seQura. The problem to solve is a simplified version of our daily problems.

## Context

seQura provides e-commerce shops with a flexible payment method that allows shoppers to split their purchases in three months without any cost. In exchange, seQura earns a fee for each purchase.

When shoppers use this payment method, they pay directly to seQura. Then, seQura disburses the orders to merchants with different frequencies and pricing.

This challenge is about implementing the process of paying merchants.

## Problem statement

We have to implement a system to automate the calculation of merchants’ disbursements payouts and seQura commissions for existing, present in the CSV files, and new orders.

The system must comply with the following requirements:

- All orders must be disbursed precisely once.
- Each disbursement, the group of orders paid on the same date for a merchant, must have a unique alphanumerical `reference`.
- Orders, amounts, and fees included in disbursements must be easily identifiable for reporting purposes.

The disbursements calculation process must be completed, for all merchants, by 8:00 UTC daily, only including those merchants that fulfill the requirements to be disbursed on that day. Merchants can be disbursed daily or weekly. We will make weekly disbursements on the same weekday as their `live_on` date (when the merchant started using seQura, present in the CSV files). Disbursements groups all the orders for a merchant in a given day or week.

For each order included in a disbursement, seQura will take a commission, which will be subtracted from the merchant order value gross of the current disbursement, following this pricing:

- `1.00 %` fee for orders with an amount strictly smaller than `50 €`.
- `0.95 %` fee for orders with an amount between `50 €` and `300 €`.
- `0.85 %` fee for orders with an amount of `300 €` or more.

Remember that we are dealing with money, so we should be careful with related operations. In this case, we should round up to two decimals following.

Lastly, on the first disbursement of each month, we have to ensure the `minimum_monthly_fee` for the previous month was reached. The `minimum_monthly_fee` ensures that seQura earns at least a given amount for each merchant.

When a merchant generates less than the `minimum_monthly_fee` of orders’ commissions in the previous month, we will charge the amount left, up to the `minimum_monthy_fee` configured, as “monthly fee”. Nothing will be charged if the merchant generated more fees than the `minimum_monthly_fee`.

Charging the `minimum_monthly_fee` is out of the scope of this challenge. It is not subtracted from the disbursement commissions. Just calculate and store it for later usage.

## Data

### Merchants sample

| REFERENCE            | EMAIL                             | LIVE_ON    | DISBURSEMENT_FREQUENCY | MINIMUM_MONTHLY_FEE |
|----------------------|-----------------------------------|------------|------------------------|---------------------|
| treutel_schumm_fadel | info@treutel-schumm-and-fadel.com | 2022-01-01 | WEEKLY                 | 29.0                |
| windler_and_sons     | info@windler-and-sons.com         | 2021-05-25 | DAILY                  | 29.0                |
| mraz_and_sons        | info@mraz-and-sons.com            | 2020-03-20 | WEEKLY                 | 0.0                 |
| cummerata_llc        | info@cummerata-llc.com            | 2019-02-04 | DAILY                  | 35.0                |

You can find [merchants CSV here](https://sequra.github.io/backend-challenge/merchants.csv).

### Orders samples

| MERCHANT REFERENCE   | AMOUNT | CREATED AT |
|----------------------|--------|------------|
| treutel_schumm_fadel | 61.74  | 2023-01-01 |
| cummerata_llc        | 293.08 | 2023-01-01 |
| mraz_and_sons        | 373.33 | 2023-01-01 |
| treutel_schumm_fadel | 60.48  | 2023-01-01 |
| mraz_and_sons        | 213.97 | 2023-01-01 |

You can find [orders CSV here](https://sequra.github.io/backend-challenge/orders.csv).

We expect you to:

- Create the necessary data structures and a way to persist them for the provided data. You don’t have to follow CSV’s schema if you think another one suits you better.
- Calculate and store the disbursements following described requirements for all the orders included in the CSV, and prepare the system to do the same for new orders.
- Fill the following table and include it in the README.

| Year | 	Number of disbursements | 	Amount disbursed to merchants | 	Amount of order fees 	 | Number of monthly fees charged (From minimum monthly fee) | 	Amount of monthly fee charged (From minimum monthly fee) |
|------|--------------------------|--------------------------------|-------------------------|-----------------------------------------------------------|-----------------------------------------------------------|
| 2022 | 	365                     | 	50.023,45 €                   | 	750,34 €               | 	50 	                                                     | 650,00 €                                                  |
| 2023 | 	400                     | 	75.000,35 €                   | 	950,43 €               | 	49 	                                                     | 750,00 €                                                  |

Note that the table values are samples, not the correct numbers.

## Instructions

Please **read carefully** the challenge, and if you have **any doubts** or need extra info, please don’t hesitate to **ask** us before starting.

- Create a **README** explaining:
    - How to **set up and and run** your solution
    - An explanation of your technical choices, tradeoffs, assumptions you took, etc.
    - If you left things aside due to time constraints, explain why and how you would resolve or improve them.
- You should **consider this code ready for production** as it was a PR to be reviewed by a colleague. Also, commit as if it were a real-world feature.
- **Design, test, develop and document the code**. It should be a performant, clean, and well-structured solution.
- You **shouldn’t spend more than 6h** on the challenge.
- You can code the solution in a language of your choice. Here are some technologies we are more familiar with (in no particular order): JavaScript, Ruby, Python, Go, Elixir, Java, Kotlin, and PHP.
- Your **experience level will be taken into consideration** when evaluating.

When completed, send a zip with your README and code, including the .git folder to see the commit log, to your hiring contact.
