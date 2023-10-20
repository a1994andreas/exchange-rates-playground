package com.exchangerates.service;

import com.exchangerates.client.ExchangeRateHostClientWrapper;
import com.exchangerates.client.ExchangeRateV6ClientWrapper;
import com.exchangerates.model.ExchangeRate;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
public class ExchangeRatesService {
	private final CircuitBreakerFactory circuitBreakerFactory;
	private final ExchangeRateHostClientWrapper exchangeRateHostClientWrapper;
	private final ExchangeRateV6ClientWrapper exchangeRateV6ClientWrapper;

	public ExchangeRatesService(ExchangeRateHostClientWrapper exchangeRateHostClientWrapper, ExchangeRateV6ClientWrapper exchangeRateV6ClientWrapper,
			CircuitBreakerFactory circuitBreakerFactory) {
		this.exchangeRateHostClientWrapper = exchangeRateHostClientWrapper;
		this.exchangeRateV6ClientWrapper = exchangeRateV6ClientWrapper;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	public ExchangeRate getExchangeRate(Currency fromCurrency, Currency toCurrency) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("EXCircuitBreaker");

		return circuitBreaker.run(() -> exchangeRateHostClientWrapper.getExchangeRate(fromCurrency, toCurrency),
				throwable -> exchangeRateV6ClientWrapper.getExchangeRate(fromCurrency, toCurrency));
	}

	public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency currency) {
		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("EXCircuitBreaker");

		return circuitBreaker.run(() -> exchangeRateHostClientWrapper.getAllExchangeRatesForCurrency(currency),
				throwable -> exchangeRateV6ClientWrapper.getAllExchangeRatesForCurrency(currency));
	}

}
