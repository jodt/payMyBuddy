package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.PaymentMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
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

    /**
     * Method that retrieves all payments issued by a user.
     * Payments are returned as a page with a defined number of payments per page
     *
     * @param issuerUser
     * @param page
     * @param size
     * @return payments as paymentsDTO from the indicated page containing the number of payments requested
     * @throws ResourceNotFoundException
     */
    @Override
    public Page<PaymentDTO> getAllPayments(UserDTO issuerUser, int page, int size) throws ResourceNotFoundException {
        log.info("Start of the process to recover all user payments begins");
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).orElseThrow(() -> {
            log.error("Issuer user account not found");
            return new ResourceNotFoundException();
        });
        Pageable paging = PageRequest.of(page, size);
        int start = Math.min((int) paging.getOffset(), issuerAccount.getPayments().size());
        int end = Math.min((start + paging.getPageSize()), issuerAccount.getPayments().size());
        Page<Payment> payments = new PageImpl<>(issuerAccount.getPayments().subList(start, end), paging, issuerAccount.getPayments().size());
        log.info("Recovery {} payments and display {} payments per page", issuerAccount.getPayments().size(), size);
        Page<PaymentDTO> paymentDTOS = payments.map(payment -> paymentMapper.asPaymentDTO(payment, payment.getReceiverAccount()));
        log.info("Payments successfully recovered");
        return paymentDTOS;
    }

    /**
     * Method that takes a paymentDTO, debits the issuer account balance for the payment amount,
     * credits the payee account balance for the payment amount, and records the payment in the database
     *
     * @param payment
     * @param issuerUser
     * @return
     * @throws InsufficientBalanceException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment makePayment(PaymentDTO payment, UserDTO issuerUser) throws InsufficientBalanceException, ResourceNotFoundException {
        log.info("Start of the process to make a payment");
        Account issuerAccount = this.accountService.findAccountByUserMail(issuerUser.getMail()).orElseThrow(() -> new ResourceNotFoundException());
        Account receiverAccount = this.accountService.findAccountByUserMail(payment.getReceiverMail()).orElseThrow(() -> new ResourceNotFoundException());

        double amountPayment = payment.getAmount().doubleValue();
        double amountWithCharges = calculateAmountWithCharges(amountPayment);

        if (isBalanceSufficient(issuerAccount.getBalance(), amountWithCharges)) {
            issuerAccount.setBalance(issuerAccount.getBalance() - amountWithCharges);
        } else {
            log.error("Insufficient balance to make payment");
            throw new InsufficientBalanceException();
        }
        receiverAccount.setBalance(receiverAccount.getBalance() + amountPayment);

        Payment successPayment = paymentMapper.asPayment(payment, issuerAccount, receiverAccount);
        return this.paymentRepository.save(successPayment);
    }

    /**
     * Method use to check if there is sufficient credit balance
     * to make payment
     *
     * @param balance
     * @param payment
     * @return true if balance is sufficient or false
     */
    private boolean isBalanceSufficient(double balance, double payment) {
        return balance - payment >= 0;
    }

    /**
     * Method used to define the total amount including charges
     *
     * @param amountPayment
     * @return payment amount including charges
     */
    private double calculateAmountWithCharges(double amountPayment) {
        double charges = this.chargesCalulation(amountPayment);
        double amountWithCharges = amountPayment + charges;
        return amountWithCharges;
    }

    /**
     * Method that takes an amount and returns the amount of charges
     * for this amount (here 0.05%)
     *
     * @param amount
     * @return the amount of charges
     */
    private double chargesCalulation(double amount) {
        return amount * 5 / 100;
    }

}
