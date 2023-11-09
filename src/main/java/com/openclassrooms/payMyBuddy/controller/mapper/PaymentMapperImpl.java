package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment asPayment(PaymentDTO paymentDTO, Account issuerAccount, Account receiverAccount) {
        Payment payment = Payment.builder()
                .amount(paymentDTO.getAmount().doubleValue())
                .description(paymentDTO.getDescription())
                .issuerAccount(issuerAccount)
                .receiverAccount(receiverAccount)
                .build();

        return payment;
    }

    @Override
    public PaymentDTO asPaymentDTO(Payment payment, Account receiverEmail) {
        log.info("Map payment to paymentDto");
        PaymentDTO paymentDTO = PaymentDTO.builder()
                .amount(new BigDecimal(payment.getAmount()).setScale(2, RoundingMode.HALF_DOWN))
                .receiverMail(receiverEmail.getUser().getMail())
                .description(payment.getDescription())
                .build();

        return paymentDTO;
    }

}
