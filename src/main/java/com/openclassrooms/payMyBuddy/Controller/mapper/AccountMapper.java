package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.model.Account;

public interface AccountMapper {

    public AccountDTO asAccountDTO(Account account);

}
