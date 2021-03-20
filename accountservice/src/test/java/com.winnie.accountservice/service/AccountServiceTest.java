package com.winnie.accountservice.service;


import com.google.common.collect.ImmutableList;
import com.netflix.discovery.converters.Auto;
import com.winnie.accountservice.client.AuthServiceClient;
import com.winnie.accountservice.client.StatisticsServiceClient;
import com.winnie.accountservice.domain.*;
import com.winnie.accountservice.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceTest {

    @Mock
    private StatisticsServiceClient statisticsServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountServiceImp;

    @Before
    public void setup(){initMocks(this);
    }

    @Test
    public void ShoudSaveChange() {
        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(10));
        grocery.setCurrency(Currency.USD);
        grocery.setPeriod(TimePeriod.DAY);
        grocery.setIcon("meal");

        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);
        salary.setIcon("wallet");

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1500));
        saving.setCurrency(Currency.USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        final Account update = new Account();
        update.setName("test");
        update.setNote("test note");
        update.setIncomes(Arrays.asList(salary));
        update.setExpenses(Arrays.asList(grocery));
        update.setSaving(saving);

        final Account account = new Account();

        when(accountServiceImp.findByName("test")).thenReturn(account);

        accountServiceImp.saveChanges("test", update);

        assertEquals(update.getNote(), account.getNote());
        assertNotNull(account.getLastSeen());

        assertEquals(update.getSaving().getAmount(), account.getSaving().getAmount());
        assertEquals(update.getSaving().getCurrency(), account.getSaving().getCurrency());
        assertEquals(update.getSaving().getInterest(), account.getSaving().getInterest());
        assertEquals(update.getSaving().getDeposit(), account.getSaving().getDeposit());
        assertEquals(update.getSaving().getCapitalization(), account.getSaving().getCapitalization());

        assertEquals(update.getExpenses().size(), account.getExpenses().size());
        assertEquals(update.getIncomes().size(), account.getIncomes().size());

        assertEquals(update.getExpenses().get(0).getTitle(), account.getExpenses().get(0).getTitle());
        assertEquals(0, update.getExpenses().get(0).getAmount().compareTo(account.getExpenses().get(0).getAmount()));
        assertEquals(update.getExpenses().get(0).getCurrency(), account.getExpenses().get(0).getCurrency());
        assertEquals(update.getExpenses().get(0).getPeriod(), account.getExpenses().get(0).getPeriod());
        assertEquals(update.getExpenses().get(0).getIcon(), account.getExpenses().get(0).getIcon());

        assertEquals(update.getIncomes().get(0).getTitle(), account.getIncomes().get(0).getTitle());
        assertEquals(0, update.getIncomes().get(0).getAmount().compareTo(account.getIncomes().get(0).getAmount()));
        assertEquals(update.getIncomes().get(0).getCurrency(), account.getIncomes().get(0).getCurrency());
        assertEquals(update.getIncomes().get(0).getPeriod(), account.getIncomes().get(0).getPeriod());
        assertEquals(update.getIncomes().get(0).getIcon(), account.getIncomes().get(0).getIcon());

        verify(accountRepository, Mockito.times(1)).save(account);
        verify(statisticsServiceClient, Mockito.times(1)).updateStatistics("test", account);

    }

    ////This is error
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNoAccountsExistedWithGivenName(){
        final Account update = new Account();
        update.setIncomes(Arrays.asList(new Item()));
        update.setExpenses(Arrays.asList(new Item()));

        when(accountServiceImp.findByName("test")).thenReturn(null);
        accountServiceImp.saveChanges("test", update);
    }


}
