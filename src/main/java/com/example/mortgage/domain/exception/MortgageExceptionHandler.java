package com.example.mortgage.domain.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.mortgage.api.dto.MortgageCheckResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class MortgageExceptionHandler {

	@ExceptionHandler(RuleViolationException.class)
	public MortgageCheckResponse handleSingleViolation(RuleViolationException ex) {
		return new MortgageCheckResponse(false, null, List.of(ex.getCode()));
	}

	@ExceptionHandler(RuleViolationsException.class)
	public MortgageCheckResponse handleMultiViolations(RuleViolationsException ex) {
		List<String> codes = ex.getViolations().stream().map(RuleViolationsException.RuleViolation::getCode).toList();
		return new MortgageCheckResponse(false, null, codes);
	}

	@ExceptionHandler(RateNotFoundException.class)
	public MortgageCheckResponse handleRateNotFound(RateNotFoundException ex) {
		return MortgageCheckResponse.notOk("RATE_NOT_FOUND");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public MortgageCheckResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		List<String> reasons = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
		return MortgageCheckResponse.notOk(reasons);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public MortgageCheckResponse handleConstraintViolation(ConstraintViolationException ex) {
		List<String> reasons = ex.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.toList());
		return MortgageCheckResponse.notOk(reasons);
	}

	@ExceptionHandler(RuntimeException.class)
	public MortgageCheckResponse handleGeneric(RuntimeException ex) {
		return MortgageCheckResponse.notOk("GENERIC_ERROR");
	}
}
