package com.example.mortgage.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.mortgage.domain.model.MortgageRate;

import jakarta.annotation.PostConstruct;

@Repository
public class InMemoryMortgageRateRepository implements MortgageRateRepository {

	private final Map<Integer, MortgageRate> data = new HashMap<>();

	@Value("#{${api.mortgage.rates}}")
	private Map<Integer, BigDecimal> seedRates;

	@PostConstruct
	void seed() {
		data.clear();
		seedRates.forEach((years, ratePct) -> data.put(years, new MortgageRate(years, ratePct, Instant.now())));
	}

	@Override
	public List<MortgageRate> findAll() {
		return List.copyOf(data.values());
	}

	@Override
	public Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod) {
		return Optional.ofNullable(data.get(maturityPeriod));
	}

	@Override
	public List<Integer> findAllMaturityPeriods() {
		return new ArrayList<>(data.keySet());
	}

}
