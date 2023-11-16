package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.BankAccount;

import java.util.Optional;

public interface BankAccountService {

    public Optional<BankAccount> findBankAccountByUserMail(String mail);

    public BankAccountDTO findBankAccountDTOByUserMail(String mail);

    BankAccount save(BankAccountDTO bankAccountDTO, String mail) throws ResourceNotFoundException;

}
