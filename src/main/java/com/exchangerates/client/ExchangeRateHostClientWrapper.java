package com.exchangerates.client;

import com.exchangerates.client.feign.ExchangeRateFeignHostClient;
import com.exchangerates.model.ExchangeRate;
import com.exchangerates.model.ExchangeRateHostResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExchangeRateHostClientWrapper {
	private final ExchangeRateFeignHostClient exchangeRateFeignClient;
	private static final String ACCESS_KEY = "a3185b0c6212b372700fbc763ca585f6";
	private static final String BASE_CURRENCY = "USD";

	public ExchangeRateHostClientWrapper(ExchangeRateFeignHostClient exchangeRateFeignClient) {
		this.exchangeRateFeignClient = exchangeRateFeignClient;
	}

	@SneakyThrows public ExchangeRate getExchangeRate(Currency fromCurrency, Currency toCurrency) {

		String currencies = fromCurrency.getCurrencyCode()+","+toCurrency.getCurrencyCode();
		ExchangeRateHostResponse exchangeRateResponse = exchangeRateFeignClient.getExchangeRates(ACCESS_KEY, currencies);

		Map<String, Double> exchangeRateMap = parseExchangeRateResponse(exchangeRateResponse);
		Double fromCurrencyToUsd = exchangeRateMap.get(fromCurrency.getCurrencyCode());
		Double toCurrencyToUsd = exchangeRateMap.get(toCurrency.getCurrencyCode());

		return new ExchangeRate(fromCurrency.getCurrencyCode(), toCurrency.getCurrencyCode(), calculateExchangeRate(toCurrencyToUsd, fromCurrencyToUsd));

	}

	public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency currency) {
		ExchangeRateHostResponse exchangeRateResponse = exchangeRateFeignClient.getExchangeRates(ACCESS_KEY);

		Map<String, Double> exchangeRateMap = parseExchangeRateResponse(exchangeRateResponse);

		Double currencyToUsd = exchangeRateMap.get(currency.getCurrencyCode());

		return exchangeRateMap.entrySet().stream()
				.map( e -> new ExchangeRate(currency.getCurrencyCode(), e.getKey(), calculateExchangeRate(e.getValue(), currencyToUsd)))
				.collect(Collectors.toList());
	}

	private Map<String, Double> parseExchangeRateResponse(ExchangeRateHostResponse exchangeRateResponse) {
		Map<String, Double> exchangeRateMap = exchangeRateResponse.getQuotes().entrySet().stream()
				.collect(Collectors.toMap( e -> e.getKey().substring(BASE_CURRENCY.length())
						, Map.Entry::getValue));
		exchangeRateMap.put(BASE_CURRENCY, 1d);
		return exchangeRateMap;
	}
	private Double calculateExchangeRate(Double newCurrencyToUsd, Double baseCurrencyToUsd) {
		return newCurrencyToUsd/baseCurrencyToUsd;
	}
}
