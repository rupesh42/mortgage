package com.example.mortgage.exception;

public class RuleViolationException extends RuntimeException {
	private final String code;
	private final String detail;

	public RuleViolationException(String code, String detail) {
		super(detail);
		this.code = code;
		this.detail = detail;
	}

	public String getCode() {
		return code;
	}

	public String getDetail() {
		return detail;
	}
}
