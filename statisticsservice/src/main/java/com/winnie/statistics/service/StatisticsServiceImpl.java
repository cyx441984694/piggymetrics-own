package com.winnie.statistics.service;

import com.google.common.collect.ImmutableMap;
import com.winnie.statistics.domain.Account;
import com.winnie.statistics.domain.Currency;
import com.winnie.statistics.domain.Item;
import com.winnie.statistics.domain.Saving;
import com.winnie.statistics.domain.timeseries.DataPoint;
import com.winnie.statistics.domain.timeseries.DataPointId;
import com.winnie.statistics.domain.timeseries.ItemMetric;
import com.winnie.statistics.domain.timeseries.StatisticMetric;
import com.winnie.statistics.repository.DataPointRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class StatisticsServiceImpl implements StatisticsService{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    DataPointRepository dataPointRepository;

    @Override
    public List<DataPoint> findByAccountName(String accountName){
        Assert.hasLength(accountName);
        return dataPointRepository.findByIdAccount(accountName);
    }

    @Override
    public DataPoint save(String accountName, Account account){


        Instant instant = LocalDate.now().atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant();

        DataPointId pointId = new DataPointId(accountName, Date.from(instant));

        Set<ItemMetric> incomes = account.getIncomes().stream()
                .map(this::createItemMetric)
                .collect(Collectors.toSet());

        Set<ItemMetric> expenses = account.getExpenses().stream()
                .map(this::createItemMetric)
                .collect(Collectors.toSet());

        Map<StatisticMetric, BigDecimal> statistics = createStatisticMetrics(incomes, expenses,account.getSaving());

        DataPoint dataPoint = new DataPoint();
        dataPoint.setId(pointId);
        dataPoint.setIncomes(incomes);
        dataPoint.setExpenses(expenses);
        dataPoint.setStatistics(statistics);
        dataPoint.setRates(exchangeRatesService.getCurrentRates());

        log.debug("new datapoint has been created:{}", pointId);

        return dataPointRepository.save(dataPoint);
    }

    private Map<StatisticMetric, BigDecimal> createStatisticMetrics(Set<ItemMetric> incomes, Set<ItemMetric> expenses, Saving saving){

        BigDecimal savingAmount = exchangeRatesService.convert(saving.getCurrency(), Currency.getBase(), saving.getAmount());

        BigDecimal expensesAmount = expenses.stream()
                .map(ItemMetric::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal incomesAmount = incomes.stream()
                .map(ItemMetric::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ImmutableMap.of(
            StatisticMetric.EXPENSES_AMOUNT, expensesAmount,
            StatisticMetric.INCOME_AMOUNT, incomesAmount,
            StatisticMetric.SAVING_AMOUNT, savingAmount
        );
    }

    private ItemMetric createItemMetric(Item item){

        BigDecimal amount = exchangeRatesService
                .convert(item.getCurrency(), Currency.getBase(), item.getAmount())
                .divide(item.getPeriod().getBaseRatio(),4, RoundingMode.HALF_UP);

        return new ItemMetric(item.getTitle(), amount);
    }
}
