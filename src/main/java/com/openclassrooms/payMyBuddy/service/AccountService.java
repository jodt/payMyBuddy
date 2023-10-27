package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.model.Account;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findAccountByUserMail(String mail);

    public AccountDTO findAccountDtoByUserMail(String mail);

    public Account saveAccount(Account account);

}
