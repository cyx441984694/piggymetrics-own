package com.winnie.statistics.service;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.winnie.statistics.controller.StatisticsController;
import com.winnie.statistics.domain.*;
import com.winnie.statistics.domain.timeseries.DataPoint;
import com.winnie.statistics.domain.timeseries.ItemMetric;
import com.winnie.statistics.domain.timeseries.StatisticMetric;
import com.winnie.statistics.repository.DataPointRepository;
import org.apache.commons.compress.archivers.zip.ScatterStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private ExchangeRatesServiceImpl exchangeRatesService;

    @Mock
    private DataPointRepository dataPointRepository;

    @Before
    public void setup(){
        initMocks(this);
    }

    @Test
    public void shouldFindByAccountName(){
        final List<DataPoint> list = ImmutableList.of(new DataPoint());
        when(dataPointRepository.findByIdAccount("test")).thenReturn(list);

        List<DataPoint> result = statisticsService.findByAccountName("test");
        assertEquals(list, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithIllegalArgumentException(){
        statisticsService.findByAccountName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithNullAccountName(){
        statisticsService.findByAccountName(null);
    }

    @Test
    public void shouldSave(){
        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);

        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(500));
        grocery.setCurrency(Currency.RUB);
        grocery.setPeriod(TimePeriod.DAY);

        Item vacation = new Item();
        vacation.setTitle("Vacation");
        vacation.setAmount(new BigDecimal(3400));
        vacation.setCurrency(Currency.EUR);
        vacation.setPeriod(TimePeriod.YEAR);

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1000));
        saving.setCurrency(Currency.EUR);
        saving.setInterest(new BigDecimal(3.2));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        Account account = new Account();
        account.setIncomes(ImmutableList.of(salary));
        account.setExpenses(ImmutableList.of(grocery, vacation));
        account.setSaving(saving);

        final Map<Currency, BigDecimal> rates = ImmutableMap.of(
                Currency.EUR, new BigDecimal("0.8"),
                Currency.RUB, new BigDecimal("80"),
                Currency.USD, BigDecimal.ONE
        );

        /**
         * When
         */

        when(exchangeRatesService.convert(any(Currency.class),any(Currency.class),any(BigDecimal.class)))
                .then(i -> ((BigDecimal)i.getArgument(2))
                        .divide(rates.get(i.getArgument(0)), 4, RoundingMode.HALF_UP));

        when(exchangeRatesService.getCurrentRates()).thenReturn(rates);

        when(dataPointRepository.save(any(DataPoint.class))).then(returnsFirstArg());

        DataPoint dataPoint = statisticsService.save("test", account);

        /**
         * Then
         */

        final BigDecimal expectedExpensesAmount = new BigDecimal("17.8861");
        final BigDecimal expectedIncomesAmount = new BigDecimal("298.9802");
        final BigDecimal expectedSavingAmount = new BigDecimal("1250");

        final BigDecimal expectedNormalizedSalaryAmount = new BigDecimal("298.9802");
        final BigDecimal expectedNormalizedVacationAmount = new BigDecimal("11.6361");
        final BigDecimal expectedNormalizedGroceryAmount = new BigDecimal("6.25");

        assertEquals(dataPoint.getId().getAccount(), "test");
        assertEquals(dataPoint.getId().getDate(), Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        assertTrue(expectedExpensesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.EXPENSES_AMOUNT)) == 0);
        assertTrue(expectedIncomesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.INCOME_AMOUNT)) == 0);
        assertTrue(expectedSavingAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.SAVING_AMOUNT)) == 0);

        ItemMetric salaryItemMetric = dataPoint.getIncomes().stream()
                .filter(i -> i.getTitle().equals(salary.getTitle()))
                .findFirst().get();

        ItemMetric vacationItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(vacation.getTitle()))
                .findFirst().get();

        ItemMetric groceryItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(grocery.getTitle()))
                .findFirst().get();

        assertTrue(expectedNormalizedSalaryAmount.compareTo(salaryItemMetric.getAmount()) == 0);
        assertTrue(expectedNormalizedVacationAmount.compareTo(vacationItemMetric.getAmount()) == 0);
        assertTrue(expectedNormalizedGroceryAmount.compareTo(groceryItemMetric.getAmount()) == 0);

        System.out.println("----:"+ rates);
        System.out.println("---datapoint---:" + dataPoint.getRates());
        assertEquals(rates, dataPoint.getRates());

        verify(dataPointRepository, times(1)).save(dataPoint);
    }
}
