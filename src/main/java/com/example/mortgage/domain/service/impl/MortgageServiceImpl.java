package com.example.mortgage.domain.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.mortgage.api.dto.MortgageCheckRequest;
import com.example.mortgage.api.dto.MortgageCheckResponse;
import com.example.mortgage.domain.model.MortgageRate;
import com.example.mortgage.domain.service.MortgageService;
import com.example.mortgage.repository.MortgageRateRepository;

@Service
public class MortgageServiceImpl implements MortgageService {

	private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);

	private final MortgageRateRepository repo;

	public MortgageServiceImpl(MortgageRateRepository repo) {
		this.repo = repo;
	}

	@Override
	public List<MortgageRate> getRates() {
		return repo.findAll();
	}

	@Override
	public MortgageCheckResponse check(MortgageCheckRequest r) {
		if (r.loanValue().compareTo(r.income().multiply(BigDecimal.valueOf(4))) > 0) {
			return new MortgageCheckResponse(false, BigDecimal.ZERO.setScale(2));
		}
		if (r.loanValue().compareTo(r.homeValue()) > 0) {
			return new MortgageCheckResponse(false, BigDecimal.ZERO.setScale(2));
		}

		MortgageRate rate = repo.findByMaturityPeriod(r.maturityPeriod())
				.orElseThrow(() -> new NoSuchElementException("Rate not found for maturity " + r.maturityPeriod()));

		BigDecimal monthly = monthlyPayment(r.loanValue(), rate.interestRate(), r.maturityPeriod());
		return new MortgageCheckResponse(true, monthly);
	}

	private BigDecimal monthlyPayment(BigDecimal principal, BigDecimal annualPercent, Integer years) {
		int n = years * 12;
		if (annualPercent.compareTo(BigDecimal.ZERO) == 0) {
			return principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_EVEN);
		}
		BigDecimal r = annualPercent.divide(BigDecimal.valueOf(100), MC).divide(BigDecimal.valueOf(12), MC);
		BigDecimal onePlusR = BigDecimal.ONE.add(r, MC);
		BigDecimal pow = onePlusR.pow(n, MC);
		BigDecimal numerator = r.multiply(pow, MC);
		BigDecimal denominator = pow.subtract(BigDecimal.ONE, MC);
		BigDecimal m = principal.multiply(numerator, MC).divide(denominator, MC);
		return m.setScale(2, RoundingMode.HALF_EVEN);
	}
}
