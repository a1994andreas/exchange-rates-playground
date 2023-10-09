package com.exchangerates.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ExchangeRateResponse {
	private boolean success;
	private String terms;
	private String privacy;
	private float timestamp;
	private String source;
	private Map<String, Double> quotes;
}
