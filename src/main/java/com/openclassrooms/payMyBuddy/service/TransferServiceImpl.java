package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.TransferMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.UserAccountNotFoundException;
import com.openclassrooms.payMyBuddy.exceptions.UserBankAccountNotFoundException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.OperationEnum;
import com.openclassrooms.payMyBuddy.model.Transfer;
import com.openclassrooms.payMyBuddy.repository.TransferRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Transfer makeCreditTransfer(TransferDTO transferDTO, String mail) throws UserAccountNotFoundException, UserBankAccountNotFoundException {
        Account userAccount = null;
        BankAccount userBankAccount = null;
        Transfer creditTransfer;
        Double amountToAdd = transferDTO.getAmount().doubleValue();

        Optional<Account> account = this.accountService.findAccountByUserMail(mail);

        if (account.isPresent()) {
            userAccount = account.get();
            userAccount.setBalance(userAccount.getBalance() + amountToAdd);
        } else {
            throw new UserAccountNotFoundException();
        }

        Optional<BankAccount> bankAccount = this.bankAccountService.findBankAccountByUserMail(mail);
        if (bankAccount.isPresent()) {
            userBankAccount = bankAccount.get();
        } else {
            throw new UserBankAccountNotFoundException();
        }

        creditTransfer = this.transferMapper.asTransfer(transferDTO, userBankAccount, OperationEnum.CREDIT, userAccount);

        return transferRepository.save(creditTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public Transfer makeDebitTransfer(TransferDTO transferDTO, String mail) throws InsufficientBalanceException, UserAccountNotFoundException, UserBankAccountNotFoundException {
        Account userAccount = null;
        BankAccount userBankAccount = null;
        Transfer debitTransfer;

        Double amountToWithdraw = transferDTO.getAmount().doubleValue();

        Optional<Account> account = this.accountService.findAccountByUserMail(mail);

        if (account.isPresent()) {
            userAccount = account.get();
            if (userAccount.getBalance() - amountToWithdraw >= 0) {
                userAccount.setBalance(userAccount.getBalance() - amountToWithdraw);
            } else {
                throw new InsufficientBalanceException("Solde insuffisant pour effectuer le transfer");
            }
        } else {
            throw new UserAccountNotFoundException();
        }

        Optional<BankAccount> bankAccount = this.bankAccountService.findBankAccountByUserMail(mail);
        if (bankAccount.isPresent()) {
            userBankAccount = bankAccount.get();
        } else {
            throw new UserBankAccountNotFoundException();
        }

        debitTransfer = this.transferMapper.asTransfer(transferDTO, userBankAccount, OperationEnum.DEBIT, userAccount);

        return transferRepository.save(debitTransfer);
    }

}
