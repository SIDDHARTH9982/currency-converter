package com.siddharth.currency_converter.service;
import com.siddharth.currency_converter.dto.ConversionResponse;
import com.siddharth.currency_converter.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyService {

    private final RestClient restClient;
    private final String baseUrl;

    public CurrencyService(
            RestClient.Builder restClientBuilder,
            @Value("${exchange.api.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;
    }

    public ConversionResponse convert(String from, String to, BigDecimal amount) {
        ExchangeRateResponse response = restClient
                .get()
                .uri(baseUrl + "/{from}", from.toUpperCase())
                .retrieve()
                .body(ExchangeRateResponse.class);

        if (response == null || response.rates() == null) {
            throw new IllegalStateException("Unable to fetch exchange rates");
        }

        Double rateValue = response.rates().get(to.toUpperCase());

        if (rateValue == null) {
            throw new IllegalArgumentException("Unsupported target currency: " + to);
        }

        BigDecimal rate = BigDecimal.valueOf(rateValue);
        BigDecimal convertedAmount = amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        return new ConversionResponse(
                from.toUpperCase(),
                to.toUpperCase(),
                amount,
                rate,
                convertedAmount
        );
    }
}