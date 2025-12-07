package com.example.mortgage.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record MortgageRate(Integer maturityPeriod, BigDecimal interestRate, Instant lastUpdate) {
}
