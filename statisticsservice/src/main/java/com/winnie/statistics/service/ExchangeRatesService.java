package com.winnie.statistics.service;


import com.winnie.statistics.domain.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRatesService {

    Map<Currency, BigDecimal> getCurrentRates();


    BigDecimal convert(Currency from, Currency to, BigDecimal amount);
}
