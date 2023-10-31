# 2. using spring boot and postgresql

Date: 2023-10-31

## Status

Accepted

## Context

The challenge contains multiple requirements that involves infrastructure and transactions to ensure data consistency.


## Decision

Spring Boot is a framework that provides a lot of features to solve these problems. It is a mature framework with a lot of documentation and community support, plus I am familiar with it.

Postgres is well known database that will provide the right levels of consistency and performance for this challenge.

Spring Boot provides good tooling to integrate with Postgres, along with Testcontainers to ensure a good integration tests.

This is a key decision, as ensuring the data integrity at testing level will provide a higher confidence in the code.

## Consequences

I wouldn't be able to show a lower level understanding of Java per se, but I think that the challenge is more about the business logic, infrastructure, and developer experience than the language itself.

The solution will be coupled to Spring Boot, and it might impact the reviewers that are not familiar with it. But, I understand that
there is good documentation out there and we can resolve some of the questions during the pairing interview.

## Alternatives took into account

- Don't use any framework.

This would require to implement a lot of features that are already provided by Spring Boot.
I decided to go the Spring Boot way to focus on the business logic, and not creating a lightweight framework for the challenge.

- Use an in memory database

I could have used H2 or another in memory database, but I decided to use Postgres to have a more realistic environment.
The goal is to provide a solution that could be used in production, so using Postgres is a better choice.

Using a different database in local tests than in production could cause multiple issues, so I decided to use Postgres in both environments.