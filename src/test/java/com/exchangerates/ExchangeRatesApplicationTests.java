package com.exchangerates;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest
class ExchangeRatesApplicationTests {
	@Autowired
	MockMvc mockMvc;

	private static WireMockServer wireMockServer;

	@BeforeAll
	static void startWireMock() {
		wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
				.port(56656)
				.usingFilesUnderDirectory("src/test/resources/wiremock"));
		wireMockServer.start();
	}

	@AfterAll
	static void stopWireMock() {
		wireMockServer.stop();
	}

	@SneakyThrows @Test
	void exchangeRatesSimpleTest() {
		wireMockServer.stubFor(WireMock.get(urlPathMatching("/live/*"))
				.withQueryParam("access_key", equalTo("a3185b0c6212b372700fbc763ca585f6"))
				.withQueryParam("currencies", equalTo("EUR,JMD"))
				.willReturn(aResponse()
						.withBodyFile("host_client_success_response.json")
//						.withBody("""
//								{
//								  "success": true,
//								  "terms": "https://currencylayer.com/terms",
//								  "privacy": "https://currencylayer.com/privacy",
//								  "timestamp": 1697790723,
//								  "source": "USD",
//								  "quotes": {
//								    "USDEUR": 0.944645,
//								    "USDJMD": 155.351832
//								  }
//								}
//								""")
						.withHeader("Content-Type", "application/json")
						.withStatus(OK.value())));

		mockMvc.perform(get("/exchange-rates?fromCurrency=EUR&toCurrency=JMD"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fromCurrency").value("EUR"))
				.andExpect(jsonPath("$.toCurrency").value("JMD"))
				.andExpect(jsonPath("$.value").value("164.45525250226277"));
	}

	@SneakyThrows @Test
	void exchangeRatesFallbackTest() {
		wireMockServer.stubFor(WireMock.get(urlPathMatching("/live/*"))
				.withQueryParam("access_key", equalTo("a3185b0c6212b372700fbc763ca585f6"))
				.withQueryParam("currencies", equalTo("EUR,JMD"))
				.willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())));

		wireMockServer.stubFor(WireMock.get(urlPathTemplate("/v6/{access_key}/latest/{baseCurrency}"))
				.withPathParam("access_key", equalTo("f0e506896c2b20379abe810d"))
				.withPathParam("baseCurrency", equalTo("EUR"))
				.willReturn(aResponse()
						.withBodyFile("v6_client_success_response.json")
						.withHeader("Content-Type", "application/json")
						.withStatus(OK.value())));

		mockMvc.perform(get("/exchange-rates?fromCurrency=EUR&toCurrency=JMD"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fromCurrency").value("EUR"))
				.andExpect(jsonPath("$.toCurrency").value("JMD"))
				.andExpect(jsonPath("$.value").value("163.1413"));
	}


	@SneakyThrows @Test
	void exchangeRatesAllSimpleTest() {
		wireMockServer.stubFor(WireMock.get(urlPathMatching("/live/*"))
				.withQueryParam("access_key", equalTo("a3185b0c6212b372700fbc763ca585f6"))
				.willReturn(aResponse()
						.withBodyFile("host_client_success_response_all.json")
						.withHeader("Content-Type", "application/json")
						.withStatus(OK.value())));

		mockMvc.perform(get("/exchange-rates/all?currency=EUR"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[0].toCurrency").value("FJD"))
				.andExpect(jsonPath("$[0].value").value("2.4229996294139444"))
				.andExpect(jsonPath("$[1].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[1].toCurrency").value("MXN"))
				.andExpect(jsonPath("$[1].value").value("19.444102917041665"))
				.andExpect(jsonPath("$[2].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[2].toCurrency").value("STD"))
				.andExpect(jsonPath("$[2].value").value("21915.38038858595"));
	}

	@SneakyThrows @Test
	void exchangeRatesAllFallbackTest() {
		wireMockServer.stubFor(WireMock.get(urlPathMatching("/live/*"))
				.withQueryParam("access_key", equalTo("a3185b0c6212b372700fbc763ca585f6"))
				.willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())));

		wireMockServer.stubFor(WireMock.get(urlPathTemplate("/v6/{access_key}/latest/{baseCurrency}"))
				.withPathParam("access_key", equalTo("f0e506896c2b20379abe810d"))
				.withPathParam("baseCurrency", equalTo("EUR"))
				.willReturn(aResponse()
						.withBodyFile("v6_client_success_response.json")
						.withHeader("Content-Type", "application/json")
						.withStatus(OK.value())));

		mockMvc.perform(get("/exchange-rates/all?currency=EUR"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[0].toCurrency").value("EUR"))
				.andExpect(jsonPath("$[0].value").value("1.0"))
				.andExpect(jsonPath("$[1].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[1].toCurrency").value("AED"))
				.andExpect(jsonPath("$[1].value").value("3.8823"))
				.andExpect(jsonPath("$[2].fromCurrency").value("EUR"))
				.andExpect(jsonPath("$[2].toCurrency").value("AFN"))
				.andExpect(jsonPath("$[2].value").value("79.4717"));
	}
}
