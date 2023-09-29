package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDTO asAccountDTO(Account account) {
        return AccountDTO.builder()
                .balance(new BigDecimal(account.getBalance()).setScale(2))
                .build();
    }

}
