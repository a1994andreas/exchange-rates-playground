package com.exchangerates.client.feign;

import com.exchangerates.model.ExchangeRateV6Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId= "exchangeRatesV6", value = "exchangeRatesV6", url = "${feign.v6.client.url}")
public interface ExchangeRateFeignV6Client {

	@GetMapping("/v6/{access_key}/latest/{baseCurrency}")
	ExchangeRateV6Response getExchangeRates(@PathVariable("access_key") String accessKey,
			@PathVariable("baseCurrency") String baseCurrency);

}
