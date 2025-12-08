package com.example.mortgage.domain.exception;

public class RateNotFoundException extends RuntimeException {
	private final Integer maturityPeriod;

	public RateNotFoundException(Integer maturityPeriod) {
		super("Rate not found for maturity " + maturityPeriod);
		this.maturityPeriod = maturityPeriod;
	}

	public Integer getMaturityPeriod() {
		return maturityPeriod;
	}
}
