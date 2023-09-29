package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.Controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.PaymentMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import com.openclassrooms.payMyBuddy.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final AccountService accountService;

    private final PaymentMapper paymentMapper;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(AccountService accountService, PaymentMapper paymentMapper, PaymentRepository paymentRepository) {
        this.accountService = accountService;
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment makePayment(PaymentDTO payment, UserDTO issuerUser) {
        Account issuerAccount = this.accountService.findByUserMail(issuerUser.getMail()).get();
        Account receiverAccount = this.accountService.findByUserMail(payment.getReceiverMail()).get();

        double amountPayment = payment.getAmount();
        issuerAccount.setBalance(issuerAccount.getBalance() - amountPayment);
        receiverAccount.setBalance(receiverAccount.getBalance() + amountPayment);

        this.accountService.saveAccount(issuerAccount);
        this.accountService.saveAccount(receiverAccount);

        Payment successPayment = paymentMapper.asPayment(payment, issuerAccount, receiverAccount);
        return this.paymentRepository.save(successPayment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(UserDTO issuerUser, int page, int size) {
        Account issuerAccount = this.accountService.findByUserMail(issuerUser.getMail()).get();
        Page<Payment> payments = this.paymentRepository.findByIssuerAccountId(issuerAccount.getId(), PageRequest.of(page, size));
        Page<PaymentDTO> paymentDTOS = payments.map(payment -> paymentMapper.asPaymentDTO(payment, payment.getReceiverAccount()));
        return paymentDTOS;
    }

}
