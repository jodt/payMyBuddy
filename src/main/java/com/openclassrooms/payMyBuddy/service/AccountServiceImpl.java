package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public Optional<Account> findByUserMail(String mail) {
        return this.accountRepository.findByUserMail(mail);
    }

    @Override
    public Account saveAccount(Account account) {
        return this.accountRepository.save(account);
    }

}
