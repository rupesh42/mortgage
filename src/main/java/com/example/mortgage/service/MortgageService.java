package com.example.mortgage.service;

import java.util.List;

import com.example.mortgage.api.dto.MortgageCheckRequest;
import com.example.mortgage.api.dto.MortgageCheckResponse;
import com.example.mortgage.domain.model.MortgageRate;

public interface MortgageService {
	List<MortgageRate> getRates();

	MortgageCheckResponse check(MortgageCheckRequest request);
}
