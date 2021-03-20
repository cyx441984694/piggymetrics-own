package com.winnie.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sun.security.auth.UserPrincipal;
import com.winnie.statistics.domain.*;
import com.winnie.statistics.domain.timeseries.DataPoint;
import com.winnie.statistics.domain.timeseries.DataPointId;
import com.winnie.statistics.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.file.attribute.AclEntry;

import static org.assertj.core.util.DateUtil.now;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private StatisticsController statisticsController;

    @Mock
    private StatisticsService statisticsService;

    private MockMvc mvc;

    @Before
    public void setup(){
        initMocks(this);
        this.mvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    public void shouldGetStatisticsByAccountName() throws Exception{
        DataPoint datapoint = new DataPoint();
        datapoint.setId(new DataPointId("test", now()));
        String account = datapoint.getId().getAccount();

        when(statisticsService.findByAccountName(account)).thenReturn(ImmutableList.of(datapoint));

        String json = mapper.writeValueAsString(datapoint);

        mvc.perform(get("/current").principal(new UserPrincipal(account)).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldGetCurrentAccountStatistics() throws Exception{
        DataPoint datapoint = new DataPoint();
        datapoint.setId(new DataPointId("test", now()));

        String account = datapoint.getId().getAccount();

        when(statisticsService.findByAccountName(account)).thenReturn(ImmutableList.of(datapoint));

        mvc.perform(get("/" + account))
                .andExpect(jsonPath("$[0].id.account").value(account))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveAccountStatistics() throws Exception{

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1500));
        saving.setCurrency(Currency.USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(10));
        grocery.setCurrency(Currency.USD);
        grocery.setPeriod(TimePeriod.DAY);

        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);

        final Account account = new Account();
        account.setSaving(saving);
        account.setExpenses(ImmutableList.of(grocery));
        account.setIncomes(ImmutableList.of(salary));

        String json = mapper.writeValueAsString(account);

        mvc.perform(put("/current").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).save(anyString(), any(Account.class));
    }

}
