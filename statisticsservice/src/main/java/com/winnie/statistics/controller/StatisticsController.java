package com.winnie.statistics.controller;


import com.winnie.statistics.domain.Account;
import com.winnie.statistics.service.StatisticsService;
import com.winnie.statistics.service.StatisticsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.winnie.statistics.domain.timeseries.DataPoint;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public List<DataPoint> getCurrentAccountStatistics(Principal principal){
        return statisticsService.findByAccountName(principal.getName());
    }

    @PreAuthorize("#oauth2.hasScope('server') or #accountName.equals('demo')")
    @RequestMapping(value = "/{accountName}", method = RequestMethod.GET)
    public List<DataPoint> getStatisticsByAccountName(@PathVariable String accountName){
        return statisticsService.findByAccountName(accountName);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/{accountName}", method = RequestMethod.PUT)
    public void saveAccountStatistics(@PathVariable String accountName, @Valid @RequestBody Account account){
        statisticsService.save(accountName, account);
    }

}
