package com.example.mortgage.domain.exception;

import java.util.List;

public class DomainValidationException extends RuntimeException {
	private final List<String> errors;

	public DomainValidationException(List<String> errors) {
		super("Validation failed");
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}
}
