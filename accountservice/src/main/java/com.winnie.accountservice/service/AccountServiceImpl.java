package com.winnie.accountservice.service;

import com.winnie.accountservice.client.AuthServiceClient;
import com.winnie.accountservice.client.StatisticsServiceClient;
import com.winnie.accountservice.domain.Account;
import com.winnie.accountservice.domain.Currency;
import com.winnie.accountservice.domain.Saving;
import com.winnie.accountservice.domain.User;
import com.winnie.accountservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public Account findByName(String accountName){
        Assert.hasLength(accountName);
        return accountRepository.findByName(accountName);
    }

    @Override
    public Account create(User user){

        Account existing = accountRepository.findByName(user.getUsername());
        Assert.isNull(existing, "account already exists: " + user.getUsername());

        authServiceClient.createUser(user);

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(0));
        saving.setCurrency(Currency.getDefault());
        saving.setCapitalization(false);
        saving.setDeposit(false);
        saving.setInterest(new BigDecimal(0));

        Account account = new Account();
        account.setName(user.getUsername());
        account.setLastSeen(new Date());
        account.setSaving(saving);

        accountRepository.save(account);

        log.info("New account is created: "+ account.getName());
        return account;
    }

    @Override
    public void saveChanges(String name, Account update){

        Account account = accountRepository.findByName(name);
        Assert.notNull(account, "cant find account with name: " + name);

        account.setIncomes(update.getIncomes());
        account.setSaving(update.getSaving());
        account.setExpenses(update.getExpenses());
        account.setNote(update.getNote());
        account.setLastSeen(new Date());
        accountRepository.save(account);

        log.debug("New account changes {} has been updated.", name);

        statisticsServiceClient.updateStatistics(name,account);

    }
}
