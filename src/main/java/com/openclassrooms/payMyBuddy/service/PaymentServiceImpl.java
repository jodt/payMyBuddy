package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.PaymentMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.UserAccountNotFoundException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import com.openclassrooms.payMyBuddy.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    public Payment makePayment(PaymentDTO payment, UserDTO issuerUser) throws InsufficientBalanceException, UserAccountNotFoundException {
        log.info("start of the process to make a payment");
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).orElseThrow(() -> new UserAccountNotFoundException());
        Account receiverAccount = this.accountService.findAccountByUserMail(payment.getReceiverMail()).orElseThrow(() -> new UserAccountNotFoundException());

        double amountPayment = payment.getAmount().doubleValue();
        double charges = this.chargesCalulation(amountPayment);
        double amountWithCharges = amountPayment + charges;

        if (isBalanceSufficient(issuerAccount.getBalance(), amountWithCharges)) {
            issuerAccount.setBalance(issuerAccount.getBalance() - amountWithCharges);
        } else {
            log.error("insufficient balance to make payment");
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
        log.info("start of the process to recover all user payments begins");
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).get();
        Pageable paging = PageRequest.of(page, size);
        int start = Math.min((int) paging.getOffset(), issuerAccount.getPayments().size());
        int end = Math.min((start + paging.getPageSize()), issuerAccount.getPayments().size());
        Page<Payment> payments = new PageImpl<>(issuerAccount.getPayments().subList(start, end), paging, issuerAccount.getPayments().size());
        log.info("secovery of the {} payments on page {}", size, page);
        Page<PaymentDTO> paymentDTOS = payments.map(payment -> paymentMapper.asPaymentDTO(payment, payment.getReceiverAccount()));
        log.info("payments successfully recovered");
        return paymentDTOS;
    }

    private double chargesCalulation(double amount) {
        return amount * 5 / 100;
    }

    private boolean isBalanceSufficient(double balance, double payment) {
        return balance - payment >= 0;
    }

}
