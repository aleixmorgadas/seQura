package dev.aleixmorgadas.orders;

import java.time.LocalDate;
import java.util.List;

public record OrderIngestedEvent(List<Order> orders, LocalDate createdAt) {}
