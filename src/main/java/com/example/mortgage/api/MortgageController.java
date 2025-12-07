package com.example.mortgage.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mortgage.api.dto.MortgageCheckRequest;
import com.example.mortgage.api.dto.MortgageCheckResponse;
import com.example.mortgage.api.dto.MortgageRateDto;
import com.example.mortgage.domain.service.MortgageService;
import com.example.mortgage.mapper.MortgageMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class MortgageController {

	private final MortgageService service;
	private final MortgageMapper mapper;

	public MortgageController(MortgageService service, MortgageMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping("/interest-rates")
	public ResponseEntity<List<MortgageRateDto>> getRates() {
		var dtos = service.getRates().stream().map(mapper::toDto).toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping("/mortgage-check")
	public MortgageCheckResponse check(@Valid @RequestBody MortgageCheckRequest request) {
		return service.check(request);
	}
}
