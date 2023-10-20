package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.OperationEnum;
import com.openclassrooms.payMyBuddy.model.Transfer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransferMapperImpl implements TransferMapper {


    @Override
    public Transfer asTransfer(TransferDTO transferDTO, BankAccount bankAccount, OperationEnum operationEnum, Account account) {
        return Transfer.builder()
                .operation(operationEnum)
                .amount(transferDTO.getAmount().doubleValue())
                .bankAccount(bankAccount)
                .account(account)
                .build();
    }

    @Override
    public TransferDTO asTransferDTO(Transfer transfer) {
        return TransferDTO.builder()
                .amount(new BigDecimal(transfer.getAmount()).setScale(2, RoundingMode.HALF_DOWN))
                .build();
    }

}
