package com.example.mortgage.repository;

import java.util.List;
import java.util.Optional;

import com.example.mortgage.domain.model.MortgageRate;

public interface MortgageRateRepository {
	List<MortgageRate> findAll();

	Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod);

	List<Integer> findAllMaturityPeriods();
}
