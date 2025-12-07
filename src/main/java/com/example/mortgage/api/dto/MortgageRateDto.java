package com.example.mortgage.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record MortgageRateDto(Integer maturityPeriod, BigDecimal interestRate, OffsetDateTime lastUpdate) {
}
