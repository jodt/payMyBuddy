package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDTO asAccountDTO(Account account) {
        return AccountDTO.builder()
                .balance(new BigDecimal(account.getBalance()).setScale(2, RoundingMode.HALF_DOWN))
                .build();
    }

}
