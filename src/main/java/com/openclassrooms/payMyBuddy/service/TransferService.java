package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.UserAccountNotFoundException;
import com.openclassrooms.payMyBuddy.exceptions.UserBankAccountNotFoundException;
import com.openclassrooms.payMyBuddy.model.Transfer;

public interface TransferService {

    Transfer makeCreditTransfer(TransferDTO transferDTO, String mail) throws UserAccountNotFoundException, UserBankAccountNotFoundException;

    Transfer makeDebitTransfer(TransferDTO transferDTO, String mail) throws InsufficientBalanceException, UserAccountNotFoundException, UserBankAccountNotFoundException;

}
