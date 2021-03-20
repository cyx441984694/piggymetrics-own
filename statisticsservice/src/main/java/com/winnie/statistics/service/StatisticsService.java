package com.winnie.statistics.service;

import com.winnie.statistics.domain.Account;
import com.winnie.statistics.domain.timeseries.DataPoint;

import java.util.List;

public interface StatisticsService {

    List<DataPoint> findByAccountName(String accountName);

    DataPoint save(String accountName, Account account);
}