package com.example.mortgage.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.mortgage.domain.model.MortgageRate;

import jakarta.annotation.PostConstruct;

@Repository
public class InMemoryMortgageRateRepository implements MortgageRateRepository {

	private final Map<Integer, MortgageRate> data = new HashMap<>();

	@PostConstruct
	void seed() {
		data.put(10, new MortgageRate(10, new BigDecimal("3.40"), Instant.now()));
		data.put(20, new MortgageRate(20, new BigDecimal("3.75"), Instant.now()));
		data.put(30, new MortgageRate(30, new BigDecimal("4.05"), Instant.now()));
	}

	@Override
	public List<MortgageRate> findAll() {
		return List.copyOf(data.values());
	}

	@Override
	public Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod) {
		return Optional.ofNullable(data.get(maturityPeriod));
	}
}
