package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment asPayment(PaymentDTO paymentDTO, Account issuerAccount, Account receiverAccount) {
        Payment payment = Payment.builder()
                .amount(paymentDTO.getAmount())
                .description(paymentDTO.getDescription())
                .issuerAccount(issuerAccount)
                .receiverAccount(receiverAccount)
                .build();

        return payment;
    }

    @Override
    public PaymentDTO asPaymentDTO(Payment payment, Account receiverEmail) {
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(payment.getAmount())
                .receiverMail(receiverEmail.getUser().getMail())
                .description(payment.getDescription())
                .build();

        return paymentDTO;
    }

}
