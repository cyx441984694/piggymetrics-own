package com.winnie.statistics.domain;


import com.esotericsoftware.kryo.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public class Item {

    @NotNull
    @Length(min = 1, max = 20)
    private String title;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private TimePeriod period;


    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public BigDecimal getAmount(){return amount;}
    public void setAmount(BigDecimal amount){this.amount = amount;}

    public TimePeriod getPeriod(){return period;}
    public void setPeriod(TimePeriod period){this.period=period;}


    public Currency getCurrency(){return currency;}
    public void setCurrency(Currency currency){this.currency = currency;}
}
