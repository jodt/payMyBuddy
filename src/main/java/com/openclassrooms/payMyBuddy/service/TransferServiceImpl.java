package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.TransferMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.OperationEnum;
import com.openclassrooms.payMyBuddy.model.Transfer;
import com.openclassrooms.payMyBuddy.repository.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@Service
public class TransferServiceImpl implements TransferService {

    private final AccountService accountService;

    private final BankAccountService bankAccountService;

    private final TransferMapper transferMapper;

    private final TransferRepository transferRepository;

    public TransferServiceImpl(AccountService accountService, BankAccountService bankAccountService, TransferMapper transferMapper, TransferRepository transferRepository) {
        this.accountService = accountService;
        this.bankAccountService = bankAccountService;
        this.transferMapper = transferMapper;
        this.transferRepository = transferRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transfer makeCreditTransfer(TransferDTO transferDTO, String mail) throws ResourceNotFoundException {
        Account userAccount = null;
        BankAccount userBankAccount = null;
        Transfer creditTransfer;
        Double amountToAdd = transferDTO.getAmount().doubleValue();

        Optional<Account> account = this.accountService.findAccountByUserMail(mail);

        if (account.isPresent()) {
            log.info("user account recovery");
            userAccount = account.get();
            userAccount.setBalance(userAccount.getBalance() + amountToAdd);
            log.info("account successfully credited in the amount of {}", amountToAdd);
        } else {
            log.error("user account not found");
            throw new ResourceNotFoundException();
        }

        Optional<BankAccount> bankAccount = this.bankAccountService.findBankAccountByUserMail(mail);
        if (bankAccount.isPresent()) {
            log.info("user's bank account recovery");
            userBankAccount = bankAccount.get();
        } else {
            log.error("user's bank account not found");
            throw new ResourceNotFoundException();
        }

        creditTransfer = this.transferMapper.asTransfer(transferDTO, userBankAccount, OperationEnum.CREDIT, userAccount);

        log.info("credit transfer carried out successfully");
        return transferRepository.save(creditTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public Transfer makeDebitTransfer(TransferDTO transferDTO, String mail) throws InsufficientBalanceException, ResourceNotFoundException {
        Account userAccount = null;
        BankAccount userBankAccount = null;
        Transfer debitTransfer;

        Double amountToWithdraw = transferDTO.getAmount().doubleValue();

        Optional<Account> account = this.accountService.findAccountByUserMail(mail);

        if (account.isPresent()) {
            log.info("user account recovery");
            userAccount = account.get();
            if (userAccount.getBalance() - amountToWithdraw >= 0) {
                userAccount.setBalance(userAccount.getBalance() - amountToWithdraw);
                log.info("account successfully debited in the amount of {}", amountToWithdraw);
            } else {
                log.error("insufficient balance to make this transfer");
                throw new InsufficientBalanceException("Solde insuffisant pour effectuer le transfer");
            }
        } else {
            log.error("user account not found");
            throw new ResourceNotFoundException();
        }

        Optional<BankAccount> bankAccount = this.bankAccountService.findBankAccountByUserMail(mail);
        if (bankAccount.isPresent()) {
            userBankAccount = bankAccount.get();
        } else {
            log.error("user's bank account not found");
            throw new ResourceNotFoundException();
        }

        debitTransfer = this.transferMapper.asTransfer(transferDTO, userBankAccount, OperationEnum.DEBIT, userAccount);

        log.info("debit transfer successfully completed");
        return transferRepository.save(debitTransfer);
    }

}
