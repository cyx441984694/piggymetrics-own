package com.winnie.accountservice.client;


import com.winnie.accountservice.domain.Account;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"feign.hystrix.enabled = true"})
public class StatisticsServiceClientFallbackTest {

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Rule
    public final OutputCapture outputCapture = new OutputCapture();

    @Before
    public void setup(){
        outputCapture.reset();
    }

    @Test
    public void shouldUpdateWithFailBack(){
        statisticsServiceClient.updateStatistics("test", new Account());
        outputCapture.expect(containsString("Error during updating statistics for account: test"));
    }

}
