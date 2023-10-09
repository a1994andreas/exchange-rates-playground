package com.exchangerates.client;

import com.exchangerates.exception.FailedToRetrieveExchangesException;
import com.exchangerates.model.ExchangeRate;
import com.exchangerates.model.ExchangeRateResponse;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExchangeRateClient {
	private final WebClient webClient;
	private static final String BASE_EXCHANGE_RATES_URL = "http://api.exchangerate.host/live";
	private static final String ACCESS_KEY = "a3185b0c6212b372700fbc763ca585f6";
	private static final String BASE_CURRENCY = "USD";

	public ExchangeRateClient(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl(BASE_EXCHANGE_RATES_URL).build();
	}

	@SneakyThrows public ExchangeRate getExchangeRate(Currency fromCurrency, Currency toCurrency) {
		ExchangeRateResponse exchangeRateResponse = this.webClient.get().uri(
						uriBuilder -> uriBuilder
								.queryParam("access_key", ACCESS_KEY)
								.queryParam("currencies", fromCurrency.getCurrencyCode()+","+toCurrency.getCurrencyCode())
								.build())
				.retrieve().bodyToMono(ExchangeRateResponse.class).block();

		if(exchangeRateResponse == null)
			throw new FailedToRetrieveExchangesException();

		Map<String, Double> exchangeRateMap = parseExchangeRateResponse(exchangeRateResponse);
		Double fromCurrencyToUsd = exchangeRateMap.get(fromCurrency.getCurrencyCode());
		Double toCurrencyToUsd = exchangeRateMap.get(toCurrency.getCurrencyCode());

		return new ExchangeRate(fromCurrency.getCurrencyCode(), toCurrency.getCurrencyCode(), calculateExchangeRate(toCurrencyToUsd, fromCurrencyToUsd));

	}

	@SneakyThrows public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency currency) {
		ExchangeRateResponse exchangeRateResponse = this.webClient.get().uri(
						uriBuilder -> uriBuilder
								.queryParam("access_key", ACCESS_KEY)
								.build())
				.retrieve().bodyToMono(ExchangeRateResponse.class).block();

		if(exchangeRateResponse == null)
			throw new FailedToRetrieveExchangesException();

		Map<String, Double> exchangeRateMap = parseExchangeRateResponse(exchangeRateResponse);

		Double currencyToUsd = exchangeRateMap.get(currency.getCurrencyCode());

		return exchangeRateMap.entrySet().stream()
				.map( e -> new ExchangeRate(currency.getCurrencyCode(), e.getKey(), calculateExchangeRate(e.getValue(), currencyToUsd)))
				.collect(Collectors.toList());
	}

	private Map<String, Double> parseExchangeRateResponse(ExchangeRateResponse exchangeRateResponse) {
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
