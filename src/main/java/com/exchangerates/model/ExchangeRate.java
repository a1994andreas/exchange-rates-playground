package com.exchangerates.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExchangeRate {
	private String fromCurrency;
	private String toCurrency;
	private Double value;
}
