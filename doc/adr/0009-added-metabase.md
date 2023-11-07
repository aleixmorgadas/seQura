# 9. added metabase

Date: 2023-11-07

## Status

Accepted

## Context

We need a way to visualize and analyze the data we crated.

## Decision

Instead of creating a set of endpoints to extract the data in different ways, I opted to
add a Metabase container that runs aside of the application that already displays all the
challenge requested information without the need of complicating the application for analytics purposes.

## Consequences

I might need to create those queries later to be part of the service.