package com.exchangerates.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExchangeRateV6Response {
	private String result;
	@JsonAlias("base_code")
	private String baseCode;
	@JsonAlias("conversion_rates")
	private Map<String, Double> conversionRates;
}
