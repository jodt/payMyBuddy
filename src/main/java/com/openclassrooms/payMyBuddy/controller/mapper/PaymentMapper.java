package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;

public interface PaymentMapper {

    public Payment asPayment(PaymentDTO paymentDTO, Account issuerAccount, Account receiverAccount);

    public PaymentDTO asPaymentDTO(Payment payment, Account receiverEmail);

}
