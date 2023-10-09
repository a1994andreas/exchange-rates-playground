package com.exchangerates.controller;

import com.exchangerates.model.ExchangeRate;
import com.exchangerates.service.ExchangeRatesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping(value = "/exchange-rates")
public class ExchangeRateController {

	private final ExchangeRatesService exchangeRatesService;

	public ExchangeRateController(ExchangeRatesService exchangeRatesService) {
		this.exchangeRatesService = exchangeRatesService;
	}

	@GetMapping
	public ExchangeRate getExchangeRate(@RequestParam(name = "fromCurrency") Currency fromCurrency, @RequestParam(name = "toCurrency") Currency toCurrency) {
		return exchangeRatesService.getExchangeRate(fromCurrency, toCurrency);
	}

	@GetMapping("/all")
	public List<ExchangeRate> getAllExchangeRatesForCurrency(@RequestParam(name = "currency") Currency currency) {
		return exchangeRatesService.getAllExchangeRatesForCurrency(currency);
	}
}
