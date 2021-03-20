package com.winnie.statistics.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.winnie.statistics.client.ExchangeRatesClient;
import com.winnie.statistics.domain.Currency;
import com.winnie.statistics.domain.ExchangeRatesContainer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ExchangeRatesServiceImplTest {

    @InjectMocks
    private ExchangeRatesServiceImpl exchangeRatesService;

    @Mock
    private ExchangeRatesClient exchangeRatesClient;


    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void shouldGetCurrentRates(){

        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(
                Currency.EUR.name(), new BigDecimal("0.8"),
                Currency.RUB.name(), new BigDecimal("80"))
        );

        when(exchangeRatesClient.getRates(Currency.getBase())).thenReturn(container);

        Map<Currency,BigDecimal> result = exchangeRatesService.getCurrentRates();
        Mockito.verify(exchangeRatesClient, times(1)).getRates(Currency.getBase());

        assertEquals(container.getRates().get(Currency.EUR.name()), result.get(Currency.EUR));
        assertEquals(container.getRates().get(Currency.RUB.name()), result.get(Currency.RUB));
        assertEquals(BigDecimal.ONE, result.get(Currency.USD));
    }

    @Test
    public void shouldConvert(){

        ExchangeRatesContainer container = new ExchangeRatesContainer();
        container.setRates(ImmutableMap.of(
                Currency.EUR.name(), new BigDecimal("0.8"),
                Currency.RUB.name(), new BigDecimal("80")
        ));
        when(exchangeRatesClient.getRates(Currency.getBase())).thenReturn(container);


        final BigDecimal amount = new BigDecimal(100);
        final BigDecimal expectedConvertionResult = new BigDecimal("1.25");

        BigDecimal result = exchangeRatesService.convert(Currency.RUB, Currency.USD, amount);

        assertTrue(expectedConvertionResult.compareTo(result)==0);


    }

}
