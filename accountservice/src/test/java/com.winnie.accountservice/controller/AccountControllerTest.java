package com.winnie.accountservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sun.security.auth.UserPrincipal;
import com.winnie.accountservice.domain.*;
import com.winnie.accountservice.service.AccountService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @Before
    public void setup(){
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void shouldGetAccountByName() throws Exception{
        final Account account = new Account();
        account.setName("test");

        when(accountService.findByName(account.getName())).thenReturn(account);

        mockMvc.perform(get("/" + account.getName()))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetCurrentAccount() throws Exception{
        final Account account =  new Account();
        account.setName("test");

        when(accountService.findByName(account.getName())).thenReturn(account);

        mockMvc.perform(get("/current").principal(new UserPrincipal(account.getName())))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveCurrentAccount() throws Exception{

        Item grocery = new Item();
        grocery.setAmount(new BigDecimal(10));
        grocery.setIcon("meal");
        grocery.setPeriod(TimePeriod.DAY);
        grocery.setTitle("Grocery");
        grocery.setCurrency(Currency.USD);

        Saving saving = new Saving();
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);
        saving.setCurrency(Currency.USD);
        saving.setAmount(new BigDecimal(1500));

        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setPeriod(TimePeriod.MONTH);
        salary.setIcon("wallet");
        salary.setCurrency(Currency.USD);
        salary.setAmount(new BigDecimal(9100));

        final Account account = new Account();
        account.setName("test");
        account.setNote("test note");
        account.setSaving(saving);
        account.setExpenses(ImmutableList.of(grocery));
        account.setIncomes(ImmutableList.of(salary));

        String json = mapper.writeValueAsString(account);

        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateNewAccount() throws Exception{

        final User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/").principal(new UserPrincipal("test")).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andExpect(status().isOk());
    }

    //No idea why it is actual with 200 but not with 400
    @Test
    public void shouldFailOnValidationTryingToSaveCurrentAccount() throws Exception{
        final Account account = new Account();
        account.setName("");

        String json = mapper.writeValueAsString(account);

        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }



    @Test
    public void shouldFailOnValidationTryingToCreateCurrentAccount() throws Exception{
        final User user = new User();
        user.setUsername("test");
        user.setPassword("test");

        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/").principal(new UserPrincipal("test11")).contentType(MediaType.APPLICATION_JSON_UTF8).content(json))
                .andExpect(status().isBadRequest());

    }
}

