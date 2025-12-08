package com.example.mortgage.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MortgageCheckRequest(@NotNull @Positive BigDecimal income, @NotNull @Positive Integer maturityPeriod,
		@NotNull @Positive BigDecimal loanValue, @NotNull @Positive BigDecimal homeValue) {
}