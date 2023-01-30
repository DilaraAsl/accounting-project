package com.cydeo.service.impl;

import com.cydeo.annotation.ExecutionTime;
import com.cydeo.client.ExchangeRateClient;
import com.cydeo.dto.common.CurrencyDto;
import com.cydeo.service.DashboardService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ExchangeRateClient exchangeRateClient;

    public DashboardServiceImpl(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    @ExecutionTime
    @Override
    public CurrencyDto listUsdExchangeRate() {

        CurrencyDto currencyDto = new CurrencyDto();

        BigDecimal euro = exchangeRateClient.getUsdExchangeRate().getUsd().getEur();
        currencyDto.setEuro(euro.setScale(2, RoundingMode.CEILING));

        BigDecimal gdb = exchangeRateClient.getUsdExchangeRate().getUsd().getGbp();
        currencyDto.setBritishPound(gdb.setScale(2, RoundingMode.CEILING));

        BigDecimal cad = exchangeRateClient.getUsdExchangeRate().getUsd().getCad();
        currencyDto.setCanadianDollar((cad).setScale(2, RoundingMode.CEILING));

        BigDecimal jpy = exchangeRateClient.getUsdExchangeRate().getUsd().getJpy();
        currencyDto.setJapaneseYen(jpy.setScale(2, RoundingMode.CEILING));

        BigDecimal inr = exchangeRateClient.getUsdExchangeRate().getUsd().getInr();
        currencyDto.setIndianRupee(inr.setScale(2, RoundingMode.CEILING));

        return currencyDto;
    }
}
