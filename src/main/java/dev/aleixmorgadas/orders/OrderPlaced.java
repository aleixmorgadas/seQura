package dev.aleixmorgadas.orders;

import java.time.LocalDate;

public record OrderPlaced(Order order, LocalDate createdAt) {}
