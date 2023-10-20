package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.User;

public interface BankAccountMapper {

    BankAccountDTO asBankAccountDTO(BankAccount bankAccount);

    BankAccount asBankAccount(BankAccountDTO bankAccountDTO, User user);

}
