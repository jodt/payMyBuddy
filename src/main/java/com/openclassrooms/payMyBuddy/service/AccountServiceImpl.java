package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public Optional<Account> findAccountByUserMail(String mail) {
        return this.accountRepository.findByUserMail(mail);
    }

    public AccountDTO findAccountDtoByUserMail(String mail) {
        Optional<AccountDTO> userAccountDTO = this.findAccountByUserMail(mail).map(account -> this.accountMapper.asAccountDTO(account));
        return userAccountDTO.orElse(null);
    }

    @Override
    public Account saveAccount(Account account) {
        return this.accountRepository.save(account);
    }

}
