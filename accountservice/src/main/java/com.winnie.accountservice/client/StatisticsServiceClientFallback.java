package com.winnie.accountservice.client;



import com.winnie.accountservice.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class StatisticsServiceClientFallback implements StatisticsServiceClient{

    private static final Logger log = LoggerFactory.getLogger(StatisticsServiceClientFallback.class);

    @Override
    public void updateStatistics(String accountName, Account account){
        log.error("Error during updating statistics for account: {}", accountName);
    }
}
