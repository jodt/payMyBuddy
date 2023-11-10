package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BankAccountMapper;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final UserService userService;

    private final BankAccountMapper bankAccountMapper;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserService userService, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public Optional<BankAccount> findBankAccountByUserMail(String mail) {
        return this.bankAccountRepository.findByUserMail(mail);
    }

    @Override
    public BankAccountDTO findBankAccountDTOByUserMail(String mail) {
        Optional<BankAccount> userBankAccount = this.findBankAccountByUserMail(mail);

        return userBankAccount
                .map(account -> this.bankAccountMapper.asBankAccountDTO(account))
                .orElse(new BankAccountDTO());

    }

    @Override
    public BankAccount save(BankAccountDTO bankAccountDTO, String mail) {
        //TODO handle exception for the optional user
        User user = this.userService.findByMail(mail).get();
        BankAccount userBankAccount = this.bankAccountMapper.asBankAccount(bankAccountDTO, user);
        log.info("User bank account successfully created");
        return this.bankAccountRepository.save(userBankAccount);
    }

}
