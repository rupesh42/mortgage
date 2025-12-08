package com.example.mortgage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.mortgage.api.dto.MortgageCheckRequest;
import com.example.mortgage.api.dto.MortgageCheckResponse;
import com.example.mortgage.domain.model.MortgageRate;
import com.example.mortgage.exception.RuleViolationsException;
import com.example.mortgage.repository.MortgageRateRepository;
import com.example.mortgage.service.MortgageServiceImpl;

@ExtendWith(MockitoExtension.class)
class MortgageServiceImplTest {

	@Mock
	private MortgageRateRepository repo;

	@InjectMocks
	private MortgageServiceImpl service;

	@Test
	@DisplayName("getRates returns repository list")
	void getRates_returnsRepoList() {
		var r10 = new MortgageRate(10, new BigDecimal("3.40"), Instant.now());
		var r20 = new MortgageRate(20, new BigDecimal("3.75"), Instant.now());
		var r30 = new MortgageRate(30, new BigDecimal("4.05"), Instant.now());

		when(repo.findAll()).thenReturn(List.of(r10, r20, r30));

		var result = service.getRates();
		assertEquals(3, result.size());
		assertEquals(10, result.get(0).maturityPeriod());
		assertEquals(new BigDecimal("4.05"), result.get(2).interestRate());
	}

	@Test
	@DisplayName("Violation: loan exceeds 4x income, maturity exists, home value ok")
	void check_loanExceedsIncomeMultiple_onlyViolation() {
		var rate30 = new MortgageRate(30, new BigDecimal("4.05"), Instant.now());
		when(repo.findByMaturityPeriod(30)).thenReturn(Optional.of(rate30));

		var req = new MortgageCheckRequest(new BigDecimal("60000"), 30, new BigDecimal("320000"),
				new BigDecimal("400000"));

		var ex = assertThrows(RuleViolationsException.class, () -> service.check(req));
		var codes = ex.getViolations().stream().map(v -> v.getCode()).toList();
		assertEquals(List.of("LOAN_EXCEEDS_INCOME_MULTIPLE"), codes);
	}

	@Test
	@DisplayName("Violation: loan exceeds home value, maturity exists, income ok")
	void check_loanExceedsHomeValue_onlyViolation() {
		var rate20 = new MortgageRate(20, new BigDecimal("3.75"), Instant.now());
		when(repo.findByMaturityPeriod(20)).thenReturn(Optional.of(rate20));

		var req = new MortgageCheckRequest(new BigDecimal("100000"), 20, new BigDecimal("320000"),
				new BigDecimal("300000"));

		var ex = assertThrows(RuleViolationsException.class, () -> service.check(req));
		var codes = ex.getViolations().stream().map(v -> v.getCode()).toList();
		assertEquals(List.of("LOAN_EXCEEDS_HOME_VALUE"), codes);
	}

	@Test
	@DisplayName("Violation: maturity more than 30 years (or not in repo) produces NO_MATURITY")
	void check_maturityMoreThan30_notFoundViolation() {
		when(repo.findByMaturityPeriod(35)).thenReturn(Optional.empty());
		when(repo.findAllMaturityPeriods()).thenReturn(List.of(10, 20, 30));

		var req = new MortgageCheckRequest(new BigDecimal("120000"), 35, new BigDecimal("300000"),
				new BigDecimal("500000"));

		var ex = assertThrows(RuleViolationsException.class, () -> service.check(req));
		var codes = ex.getViolations().stream().map(v -> v.getCode()).toList();
		assertEquals(List.of("NO_MATURITY"), codes);
	}

	@Test
	@DisplayName("Violation: all three reasons are returned together")
	void check_multipleViolations_allCodes() {
		when(repo.findByMaturityPeriod(250)).thenReturn(Optional.empty());
		when(repo.findAllMaturityPeriods()).thenReturn(List.of(10, 20, 30));

		var req = new MortgageCheckRequest(new BigDecimal("60000"), 250, new BigDecimal("320000"),
				new BigDecimal("300000"));

		var ex = assertThrows(RuleViolationsException.class, () -> service.check(req));
		var codes = ex.getViolations().stream().map(v -> v.getCode()).toList();
		assertTrue(codes.contains("NO_MATURITY"));
		assertTrue(codes.contains("LOAN_EXCEEDS_INCOME_MULTIPLE"));
		assertTrue(codes.contains("LOAN_EXCEEDS_HOME_VALUE"));
		assertEquals(3, codes.size());
	}

	@Test
	@DisplayName("Happy path: all rules pass and monthly payment is computed")
	void check_happyPath_returnsMonthlyPayment() {
		var rate30 = new MortgageRate(30, new BigDecimal("4.05"), Instant.now());
		when(repo.findByMaturityPeriod(30)).thenReturn(Optional.of(rate30));

		var req = new MortgageCheckRequest(new BigDecimal("100000"), 30, new BigDecimal("300000"),
				new BigDecimal("500000"));

		MortgageCheckResponse resp = service.check(req);
		assertNotNull(resp);
		assertTrue(resp.feasible());
		assertNotNull(resp.monthlyPayment());

		BigDecimal expected = expectedMonthly(new BigDecimal("300000"), new BigDecimal("4.05"), 30);
		assertEquals(expected, resp.monthlyPayment(), "Monthly payment should match formula output");
	}

	@Test
	void testMultipleRuleViolations() {
		var rate30 = new MortgageRate(30, BigDecimal.valueOf(4.05), Instant.now());
		when(repo.findByMaturityPeriod(30)).thenReturn(Optional.of(rate30));
		MortgageCheckRequest req = new MortgageCheckRequest(BigDecimal.valueOf(20000), 30, BigDecimal.valueOf(100000),
				BigDecimal.valueOf(90000));

		RuleViolationsException ex = assertThrows(RuleViolationsException.class, () -> service.check(req));

		List<String> codes = ex.getViolations().stream().map(v -> v.getCode()).toList();
		assertTrue(codes.contains("LOAN_EXCEEDS_INCOME_MULTIPLE"));
		assertTrue(codes.contains("LOAN_EXCEEDS_HOME_VALUE"));
		assertEquals(2, codes.size());
	}

	private BigDecimal expectedMonthly(BigDecimal principal, BigDecimal annualPercent, Integer years) {
		MathContext mathContext = new MathContext(20, RoundingMode.HALF_EVEN);
		int n = years * 12;
		if (annualPercent.compareTo(BigDecimal.ZERO) == 0) {
			return principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_EVEN);
		}
		BigDecimal r = annualPercent.divide(BigDecimal.valueOf(100), mathContext).divide(BigDecimal.valueOf(12),
				mathContext);
		BigDecimal onePlusR = BigDecimal.ONE.add(r, mathContext);
		BigDecimal pow = onePlusR.pow(n, mathContext);
		BigDecimal numerator = r.multiply(pow, mathContext);
		BigDecimal denominator = pow.subtract(BigDecimal.ONE, mathContext);
		BigDecimal m = principal.multiply(numerator, mathContext).divide(denominator, mathContext);
		return m.setScale(2, RoundingMode.HALF_EVEN);
	}

}
