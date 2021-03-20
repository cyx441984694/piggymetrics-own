package com.winnie.statistics.domain;


import com.esotericsoftware.kryo.NotNull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Document(collection = "accounts")
@JsonIgnoreProperties(ignoreUnknown =  true)
public class Account {

    @Valid
    @NotNull
    private List<Item> incomes;

    @Valid
    @NotNull
    private List<Item> expenses;

    @Valid
    @NotNull
    private Saving saving;


    public List<Item> getIncomes(){return incomes;}
    public void setIncomes(List<Item> incomes){this.incomes = incomes;}

    public List<Item> getExpenses(){return expenses;}
    public void setExpenses(List<Item> expenses){this.expenses = expenses;}

    public Saving getSaving(){return saving;}
    public void setSaving(Saving saving){this.saving = saving;}


}
