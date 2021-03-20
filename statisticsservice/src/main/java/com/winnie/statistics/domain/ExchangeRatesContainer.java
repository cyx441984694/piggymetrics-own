package com.winnie.statistics.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown =  true , value = {"date"})
public class ExchangeRatesContainer {

    private LocalDate date = LocalDate.now();

    private Currency base;

    private Map<String, BigDecimal> rates;

    public LocalDate getDate(){return date;}
    public Currency getBase(){return base;}
    public Map<String,BigDecimal> getRates(){return rates;}

    public void setDate(LocalDate date){this.date = date;}
    public void setBase(Currency base){this.base = base;}
    public void setRates(Map<String, BigDecimal> rates){this.rates = rates;}

    @Override
    public String toString(){
        return "RateList{" + "date= " + date + ", base= " + base + ", rates=" + rates + '}';}
}
