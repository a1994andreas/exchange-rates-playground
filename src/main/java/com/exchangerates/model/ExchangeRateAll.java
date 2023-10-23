package com.exchangerates.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExchangeRateAll {
	private String currencyKey;
	private Boolean allLoaded;
}
