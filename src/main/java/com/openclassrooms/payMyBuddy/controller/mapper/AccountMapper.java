package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.model.Account;

public interface AccountMapper {

    AccountDTO asAccountDTO(Account account);

}
