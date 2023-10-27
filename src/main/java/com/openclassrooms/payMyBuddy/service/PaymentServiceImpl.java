package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.PaymentMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import com.openclassrooms.payMyBuddy.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public Payment makePayment(PaymentDTO payment, UserDTO issuerUser) throws InsufficientBalanceException {
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).get();
        Account receiverAccount = this.accountService.findAccountByUserMail(payment.getReceiverMail()).get();

        double amountPayment = payment.getAmount().doubleValue();
        double charges = this.chargesCalulation(amountPayment);
        double amountWithCharges = amountPayment + charges;

        if (isBalanceSufficient(issuerAccount.getBalance(), amountWithCharges)) {
            issuerAccount.setBalance(issuerAccount.getBalance() - amountWithCharges);
        } else {
            throw new InsufficientBalanceException("Solde insuffisant pour effectuer le paiement");
        }
        receiverAccount.setBalance(receiverAccount.getBalance() + amountPayment);

        this.accountService.saveAccount(issuerAccount);
        this.accountService.saveAccount(receiverAccount);

        Payment successPayment = paymentMapper.asPayment(payment, issuerAccount, receiverAccount);
        return this.paymentRepository.save(successPayment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(UserDTO issuerUser, int page, int size) {
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).get();
        //Page<Payment> payments = this.paymentRepository.findByIssuerAccountId(issuerAccount.getId(), PageRequest.of(page, size));
        Page<Payment> payments = new PageImpl<>(issuerAccount.getPayments(), PageRequest.of(page, size), issuerAccount.getPayments().size());
        Page<PaymentDTO> paymentDTOS = payments.map(payment -> paymentMapper.asPaymentDTO(payment, payment.getReceiverAccount()));
        return paymentDTOS;
    }

    private double chargesCalulation(double amount) {
        return amount * 5 / 100;
    }

    private boolean isBalanceSufficient(double balance, double payment) {
        return balance - payment >= 0;
    }

}
