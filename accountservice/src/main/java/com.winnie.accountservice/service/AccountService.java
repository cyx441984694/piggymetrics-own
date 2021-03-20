package com.winnie.accountservice.service;

import com.winnie.accountservice.domain.Account;
import com.winnie.accountservice.domain.User;

public interface AccountService {

    Account findByName(String accountName);

    Account create(User user);

    void saveChanges(String name, Account update);
}
