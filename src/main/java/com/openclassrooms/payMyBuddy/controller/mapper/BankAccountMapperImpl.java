package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl implements BankAccountMapper {

    @Override
    public BankAccountDTO asBankAccountDTO(BankAccount bankAccount) {
        return BankAccountDTO.builder()
                .iban(bankAccount.getIban())
                .build();
    }

    @Override
    public BankAccount asBankAccount(BankAccountDTO bankAccount, User user) {
        return BankAccount.builder()
                .iban(bankAccount.getIban())
                .user(user)
                .build();
    }

}
