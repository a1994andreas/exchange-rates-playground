package com.exchangerates.service;

import com.exchangerates.client.ExchangeRateClient;
import com.exchangerates.model.ExchangeRate;
import com.exchangerates.model.ExchangeRateAll;
import com.exchangerates.repository.ExchangeRatesRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ExchangeRatesService {

	private final ExchangeRateClient exchangeRateClient;
	/*
	* Since this is an example we use in thi file both ways doing queries in mongodb
	* with a repository interface and with a mongoTemplate
	 */
	private final ExchangeRatesRepository exchangeRatesRepository;
	private final MongoTemplate mongoTemplate;

	public ExchangeRatesService(ExchangeRateClient exchangeRateClient, ExchangeRatesRepository exchangeRatesRepository, MongoTemplate mongoTemplate) {
		this.exchangeRateClient = exchangeRateClient;
		this.exchangeRatesRepository = exchangeRatesRepository;
		this.mongoTemplate = mongoTemplate;
	}

	public ExchangeRate getExchangeRate(Currency fromCurrency, Currency toCurrency) {
//		ExchangeRate exchangeRate = mongoTemplate.findOne(query(
//				where("fromCurrency").is(fromCurrency.getCurrencyCode())
//				.and("toCurrency").is(toCurrency.getCurrencyCode()))
//				, ExchangeRate.class);
		ExchangeRate exchangeRate = exchangeRatesRepository.findByFromCurrencyAndToCurrency(fromCurrency.getCurrencyCode(), toCurrency.getCurrencyCode());

		if (exchangeRate == null) {
			exchangeRate = exchangeRateClient.getExchangeRate(fromCurrency, toCurrency);
			exchangeRatesRepository.save(exchangeRate);
		}

		return exchangeRate;
	}

	public List<ExchangeRate> getAllExchangeRatesForCurrency(Currency currency) {
		ExchangeRateAll exchangeRateAll = mongoTemplate.findOne(query(where("currencyKey").is(currency.getCurrencyCode()+"_ALL")), ExchangeRateAll.class);
		List<ExchangeRate> exchangeRates;

		if (exchangeRateAll != null && exchangeRateAll.getAllLoaded().equals(Boolean.TRUE)) {
			exchangeRates = exchangeRatesRepository.findAllByFromCurrency(currency.getCurrencyCode());
		} else {
			exchangeRates = exchangeRateClient.getAllExchangeRatesForCurrency(currency);
			exchangeRatesRepository.saveAll(exchangeRates);

			mongoTemplate.insert(new ExchangeRateAll(currency.getCurrencyCode()+"_ALL", Boolean.TRUE));
		}
		return exchangeRates;
	}

}
