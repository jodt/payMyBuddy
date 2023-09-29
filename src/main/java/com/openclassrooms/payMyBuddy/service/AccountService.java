package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Account;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findByUserMail(String mail);

    public Account saveAccount(Account account);

}
