# 3. testing strategy

Date: 2023-10-31

## Status

Accepted

## Context

A feature that involves Money requires a high level of confidence that it works as expected.

The challenge has functional and non-functional requirements that need to be tested.

## Decision

We will start with E2E tests to ensure that the feature works as expected from the user perspective.

This will be a combination of [Spring Boot MVC Test][mvc-test] (HTTP layer) and [Testcontainers][tc] (data layer).

The key objective of using the combination is to create a testing life cycle that doesn't depend of a human interaction starting the database nor clean it.

Once we have the E2E tests in place, we will start with the unit tests to ensure that the business logic is correct.

The reason of outside-in TDD is to not overcomplicate our code and keep it simple.

## Consequences

The E2E tests will be slower than unit tests, but they will provide a higher confidence in the code.

Not everyone is familiar with Testcontainers, so it might be a bit harder to understand the code.
People might be familiar with [`docker compose`][dc] to start the database in local, but Testcontainers is a better choice for the user experience.

### Resources to understand Testcontainers and Spring Boot integration

- https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
- https://testcontainers.com/guides/testcontainers-container-lifecycle/

[mvc-test]: https://spring.io/guides/gs/testing-web/
[tc]: https://testcontainers.com/
[dc]: https://docs.docker.com/compose/