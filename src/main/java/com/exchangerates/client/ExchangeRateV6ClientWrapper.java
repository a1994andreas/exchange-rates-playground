package com.exchangerates.client;

import com.exchangerates.client.feign.ExchangeRateFeignV6Client;
import com.exchangerates.model.ExchangeRate;
import com.exchangerates.model.ExchangeRateV6Response;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExchangeRateV6ClientWrapper {

	private final ExchangeRateFeignV6Client exchangeRateFeignClient;
	private static final String ACCESS_KEY = "f0e506896c2b20379abe810d";

	public ExchangeRateV6ClientWrapper(ExchangeRateFeignV6Client exchangeRateFeignClient) {
		this.exchangeRateFeignClient = exchangeRateFeignClient;
	}

	public ExchangeRate getExchangeRate(Currency baseCurrency, Currency toCurrency) {
		List<ExchangeRate> exchangeRates = getAllExchangeRatesForCurrency(baseCurrency);

		Optional<ExchangeRate> exchangeRateOptional = exchangeRates.stream().filter( e -> toCurrency.getCurrencyCode().equals(e.getToCurrency())).findFirst();

		return exchangeRateOptional.orElse(null);
	}

	public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency baseCurrency) {
		ExchangeRateV6Response exchangeRateV6Response = exchangeRateFeignClient.getExchangeRates(ACCESS_KEY, baseCurrency.getCurrencyCode());

		return exchangeRateV6Response.getConversionRates().entrySet().stream()
				.map( e -> new ExchangeRate(baseCurrency.getCurrencyCode(), e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}
}
