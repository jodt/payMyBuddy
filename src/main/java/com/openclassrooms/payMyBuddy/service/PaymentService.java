package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.UserAccountNotFoundException;
import com.openclassrooms.payMyBuddy.model.Payment;
import org.springframework.data.domain.Page;

public interface PaymentService {

    public Payment makePayment(PaymentDTO paymentDTO, UserDTO userDTO) throws InsufficientBalanceException, UserAccountNotFoundException;

    public Page<PaymentDTO> getAllPayments(UserDTO issuerUser, int page, int size);

}
