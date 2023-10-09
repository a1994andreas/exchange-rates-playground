package com.exchangerates.service;

import com.exchangerates.client.ExchangeRateClient;
import com.exchangerates.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class ExchangeRatesService {

	private final ExchangeRateClient exchangeRateClient;
	public ExchangeRatesService(ExchangeRateClient exchangeRateClient) {
		this.exchangeRateClient = exchangeRateClient;
	}

	public ExchangeRate getExchangeRate(Currency fromCurrency, Currency toCurrency) {
		return exchangeRateClient.getExchangeRate(fromCurrency, toCurrency);
	}

	public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency currency) {
		return exchangeRateClient.getAllExchangeRatesForCurrency(currency);
	}

}
