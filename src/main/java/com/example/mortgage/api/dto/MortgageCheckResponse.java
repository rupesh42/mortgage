package com.example.mortgage.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record MortgageCheckResponse(boolean feasible, BigDecimal monthlyPayment, List<String> reasons) {
	public static MortgageCheckResponse ok(BigDecimal monthly) {
		return new MortgageCheckResponse(true, monthly, List.of());
	}

	public static MortgageCheckResponse notOk(String... reasons) {
		return new MortgageCheckResponse(false, null, List.of(reasons));
	}

	public static MortgageCheckResponse notOk(List<String> reasons) {
		return new MortgageCheckResponse(false, null, List.copyOf(reasons));
	}
}
