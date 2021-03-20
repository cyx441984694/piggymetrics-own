package com.winnie.statistics.client;

import com.winnie.statistics.domain.Currency;
import com.winnie.statistics.domain.ExchangeRatesContainer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


//The ${rates.url} is defined in config-statistics.yaml and also application.yml in test directory
@FeignClient(url = "${rates.url}", name = "rates-client", fallback = ExchangeRatesClientFallback.class)
public interface ExchangeRatesClient {

    @RequestMapping(method = RequestMethod.GET, value ="/latest")
    ExchangeRatesContainer getRates(@RequestParam("base") Currency base);
}
