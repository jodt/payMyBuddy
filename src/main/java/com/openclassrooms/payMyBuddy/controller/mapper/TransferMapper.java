package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.OperationEnum;
import com.openclassrooms.payMyBuddy.model.Transfer;

public interface TransferMapper {

    Transfer asTransfer(TransferDTO transferDTO, BankAccount bankAccount, OperationEnum operationEnum, Account account);

    TransferDTO asTransferDTO(Transfer transfer);

}
