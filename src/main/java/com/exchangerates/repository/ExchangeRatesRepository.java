package com.exchangerates.repository;

import com.exchangerates.model.ExchangeRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExchangeRatesRepository extends MongoRepository<ExchangeRate, String> {
	List<ExchangeRate> findAllByFromCurrency(String fromCurrency);
	ExchangeRate findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

}
