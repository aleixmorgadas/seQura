# seQura backend coding challenge

- [Challenge](./CHALLENGE.md)
- [Questions-Answers](./Questions-Answers.md)
- [ADRs](./doc/adr/)

## System requirements

- Java 17
- Docker (It needs to be Docker, other container systems like Podman might not work due to specific Testcontainers
  configuration)
- If using VS Code, you will need the [httpYAC](https://marketplace.visualstudio.com/items?itemName=anweber.vscode-httpyac) extension to run the `application.http` requests.
  - In case of using IntelliJ and alike IDE, it comes out of the box.

:information: I made the challenge on a Linux machine using different Docker features like networks, it might be that in Mac it doesn't work as expected.
This would only affect the stateful configuration that can be found later.

## Challenge outcome

![Challenge output](./doc/challenge-output.png)

| Year | 	Number of disbursements | 	Amount disbursed to merchants | 	Amount of order fees 	 | Number of monthly fees charged (From minimum monthly fee) | 	Amount of monthly fee charged (From minimum monthly fee) |
|------|--------------------------|--------------------------------|-------------------------|-----------------------------------------------------------|-----------------------------------------------------------|
| 2022 | 	1435                    | 	16.120.593,37 €               | 	151.052,97 €           | 	10 	                                                     | 180.38 €                                                  |
| 2023 | 	1021                    | 	17.283.904,62 €               | 	159.718,97 €           | 	9 	                                                      | 173.99 €                                                  |

## Running the solution in local

**Stateless solution**:

- `./gradlew bootTestRun`

This will start the application with a Postgres Container automatically on a random port.

**Stateful solution**:

- `STATEFUL=true ./gradlew bootTestRun`

**Stateful with Metabase**:

- `STATEFUL=true METABASE=true ./gradlew bootTestRun`

It requires to be stateful application in order to use Metabase.

## Running the tests in local

`./gradlew test`

It will start the postgres automatically and shut it down when the test suite finishes using Testcontainers.

### Postgres' connection information:

- Port: See `docker ps` - In case of stateless, the port will be random. In case of stateful, it will be `5555`.
- Database: `test`
- User: `test`
- Password: `test`

### Metabase credentials:

- Host: http://localhost:3000
- Email: sequra@example.com
- Password: seQura23

Dashboard with the Challenge results: http://localhost:3000/dashboard/1-default

## Executing the challenge in local

Open the file [`application.http`](application.http) that you can find in the root directory,
and execute the requests in order.

You will see some messages logs updating the context while ingesting the `orders.csv`.

And you will be able to inspect the result when it finishes at `http://localhost:8080/disbursements/by-year`

You can see the progress at Metabase in case of using the stateful solution.

## Decisions made

You can see the ADRs at [./doc/adr/](./doc/adr/) directory.

The key decisions are:

- Using Spring Boot to speed up the development.
- Splitting the solution in three different _bounded contexts_: `orders`, `merchants` which are more CRUD operations and `disbursements` which contains the rich business logic.
  - The communication between `Orders` and `Disbursements` is done using ApplicationPublisher, and internal event bus from Spring Boot.
  - In case of going to production and requiring a more robust solution, I would use Kafka or RabbitMQ, even though it might be consumed by the same application but the deployment instead of being monolithic, it would be by responsibility.
    - This would increase the deployment complexity tho. I would only do it if the business requires it.
- The `Disbursements` is separated into different repositories: `Disbursement`, `DisbursementOrder`, and `MinimumMonthlyFee` due to the need of splitting the _Aggregate_ due to performance and transactional operations.
- The `Disbursement` service access the `MerchantRepository` because it is read only operations and it doesn't affect the performance.

## To continue working on the challenge

- Perform more HTTP format validation. Right now, I don't validate the input of the external world, I just assume that it is correct.
- Add Micrometer to add metrics to the application. Not only healthy metrics of how the  service is operating, but based on the business needs.
- Configure Docker as deployment artifact.