package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BankAccountMapper;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
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


    /**
     * Method that takes a mail and return an optional bankAccount
     *
     * @param mail
     * @return optional bank account
     */
    @Override
    public Optional<BankAccount> findBankAccountByUserMail(String mail) {
        return this.bankAccountRepository.findByUserMail(mail);
    }

    /**
     * Method that takes a mail and return a bankAccountDto
     *
     * @param mail
     * @return the user bankAccountDto or a new bankAccountDTo
     */
    @Override
    public BankAccountDTO findBankAccountDTOByUserMail(String mail) {
        Optional<BankAccount> userBankAccount = this.findBankAccountByUserMail(mail);

        return userBankAccount
                .map(account -> this.bankAccountMapper.asBankAccountDTO(account))
                .orElse(new BankAccountDTO());

    }

    @Override
    public BankAccount save(BankAccountDTO bankAccountDTO, String mail) throws ResourceNotFoundException {
        User user = this.userService.findByMail(mail).orElseThrow(() -> {
            log.error("User not found");
            return new ResourceNotFoundException();
        });
        BankAccount userBankAccount = this.bankAccountMapper.asBankAccount(bankAccountDTO, user);
        log.info("User bank account successfully created");
        return this.bankAccountRepository.save(userBankAccount);
    }

}
