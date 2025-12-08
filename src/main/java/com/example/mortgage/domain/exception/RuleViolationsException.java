package com.example.mortgage.domain.exception;

import java.util.List;

public class RuleViolationsException extends RuntimeException {

	public static final class RuleViolation {
		private final String code;
		private final String detail;

		public RuleViolation(String code, String detail) {
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

	private final transient List<RuleViolation> violations;

	public RuleViolationsException(List<RuleViolation> violations) {
		super("Multiple rule violations");
		this.violations = List.copyOf(violations);
	}

	public List<RuleViolation> getViolations() {
		return violations;
	}
}
