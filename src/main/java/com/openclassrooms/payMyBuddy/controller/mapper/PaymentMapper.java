package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;

public interface PaymentMapper {

    Payment asPayment(PaymentDTO paymentDTO, Account issuerAccount, Account receiverAccount);

    PaymentDTO asPaymentDTO(Payment payment, Account receiverEmail);

}
