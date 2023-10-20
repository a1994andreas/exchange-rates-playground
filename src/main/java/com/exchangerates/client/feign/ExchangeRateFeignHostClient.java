package com.exchangerates.client.feign;

import com.exchangerates.model.ExchangeRateHostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId= "exchangeRatesHost", value = "exchangeRatesHost", url = "${feign.host.client.url}")
public interface ExchangeRateFeignHostClient {

	@GetMapping("/live")
	ExchangeRateHostResponse getExchangeRates(@RequestParam("access_key") String accessKey,
			@RequestParam("currencies") String currencies);

	@GetMapping("/live")
	ExchangeRateHostResponse getExchangeRates(@RequestParam("access_key") String accessKey);

}
