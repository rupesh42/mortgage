package com.example.mortgage.mapper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mortgage.api.dto.MortgageRateDto;
import com.example.mortgage.domain.model.MortgageRate;

@Mapper(componentModel = "spring")
public interface MortgageMapper {

	@Mapping(target = "lastUpdate", expression = "java(toOffset(rate.lastUpdate()))")
	MortgageRateDto toDto(MortgageRate rate);

	default OffsetDateTime toOffset(java.time.Instant instant) {
		return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
	}
}
