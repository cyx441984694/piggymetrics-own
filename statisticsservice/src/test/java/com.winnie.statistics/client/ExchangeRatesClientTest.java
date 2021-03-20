package com.winnie.statistics.client;


import com.winnie.statistics.domain.Currency;
import com.winnie.statistics.domain.ExchangeRatesContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeRatesClientTest {

    @Autowired
    private ExchangeRatesClient client;


    @Test
    public void shouldRetrieveExchangeRates(){
        ExchangeRatesContainer container = client.getRates(Currency.getBase());

        //From the api, it gets the yesterday date. That is why needed additional handling to get yesterday dat;
        assertEquals(container.getDate(),LocalDate.now().plusDays(-1));
        assertEquals(container.getBase(), Currency.getBase());

        assertNotNull(container.getRates());
        assertNotNull(container.getRates().get(Currency.USD.name()));
        assertNotNull(container.getRates().get(Currency.EUR.name()));
        assertNotNull(container.getRates().get(Currency.RUB.name()));


        //shouldRetrieveExchangeRatesForSpecifiedCurrency
        ///I included in here
        Currency requestedCurrency = Currency.EUR;
        assertNotNull(container.getRates().get(requestedCurrency.name()));

    }

}
