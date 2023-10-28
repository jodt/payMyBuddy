package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
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
        log.info("Try to find the account for the mail {}", mail);
        return this.accountRepository.findByUserMail(mail);
    }

    public AccountDTO findAccountDtoByUserMail(String mail) {
        log.info("Try to find the accountDTO for the email {}", mail);
        Optional<AccountDTO> userAccountDTO = this.findAccountByUserMail(mail).map(account -> this.accountMapper.asAccountDTO(account));
        return userAccountDTO.orElse(null);
    }

    @Override
    public Account saveAccount(Account account) {
        return this.accountRepository.save(account);
    }

}
