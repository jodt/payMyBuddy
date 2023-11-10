package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.Transfer;

public interface TransferService {

    Transfer makeCreditTransfer(TransferDTO transferDTO, String mail) throws ResourceNotFoundException;

    Transfer makeDebitTransfer(TransferDTO transferDTO, String mail) throws InsufficientBalanceException, ResourceNotFoundException;

}
