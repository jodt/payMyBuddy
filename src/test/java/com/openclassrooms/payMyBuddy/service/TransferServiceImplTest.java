package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.TransferMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.*;
import com.openclassrooms.payMyBuddy.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    AccountService accountService;

    @Mock
    BankAccountService bankAccountService;

    @Mock
    TransferMapper transferMapper;

    @Mock
    TransferRepository transferRepository;

    @InjectMocks
    TransferServiceImpl transferService;


    private User user;

    private TransferDTO transferDTO;

    private Account account;

    private Transfer transfer;

    private BankAccount bankAccount;

    @BeforeEach
    void init() {

        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        transferDTO = TransferDTO.builder()
                .amount(new BigDecimal("100"))
                .build();

        account = Account.builder()
                .number("123456789")
                .balance(500)
                .user(user)
                .id(1)
                .build();

        bankAccount = BankAccount.builder()
                .id(1)
                .user(user)
                .transfer(new ArrayList<>())
                .iban("FR12121212")
                .build();

        transfer = Transfer.builder()
                .account(account)
                .bankAccount(bankAccount)
                .amount(100)
                .build();
    }

    @Test
    @DisplayName("Should make transfer and credit account")
    void shouldMakeCreditTransfer() throws ResourceNotFoundException {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.of(account));
        when(this.bankAccountService.findBankAccountByUserMail(user.getMail())).thenReturn(Optional.of(bankAccount));
        when(this.transferMapper.asTransfer(transferDTO, bankAccount, OperationEnum.CREDIT, account)).thenReturn(transfer);
        when(this.transferRepository.save(transfer)).thenReturn(transfer);

        Transfer result = this.transferService.makeCreditTransfer(transferDTO, user.getMail());

        assertNotNull(result);
        assertEquals(transfer, result);
        assertEquals(500 + transfer.getAmount(), account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService).findBankAccountByUserMail(user.getMail());
        verify(transferMapper).asTransfer(transferDTO, bankAccount, OperationEnum.CREDIT, account);
        verify(transferRepository).save(transfer);

    }

    @Test
    @DisplayName("Should not make transfer -> user account not found")
    void shouldNotMakeCreditTransferUserAccountNotFound() {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.transferService.makeCreditTransfer(transferDTO, user.getMail()));

        assertEquals(500, account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService, never()).findBankAccountByUserMail(user.getMail());
        verify(transferMapper, never()).asTransfer(transferDTO, bankAccount, OperationEnum.CREDIT, account);
        verify(transferRepository, never()).save(transfer);

    }

    @Test
    @DisplayName("Should not make transfer -> user bank account not found")
    void shouldNotMakeCreditTransferUserBankAccountNotFound() throws ResourceNotFoundException {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.of(account));
        when(this.bankAccountService.findBankAccountByUserMail(user.getMail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.transferService.makeCreditTransfer(transferDTO, user.getMail()));
        assertEquals(500 + transfer.getAmount(), account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService).findBankAccountByUserMail(user.getMail());
        verify(transferMapper, never()).asTransfer(transferDTO, bankAccount, OperationEnum.CREDIT, account);
        verify(transferRepository, never()).save(transfer);

    }

    @Test
    @DisplayName("Should make a debit transfer and debit account")
    void shouldMakeDebitTransfer() throws InsufficientBalanceException, ResourceNotFoundException {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.of(account));
        when(this.bankAccountService.findBankAccountByUserMail(user.getMail())).thenReturn(Optional.of(bankAccount));
        when(this.transferMapper.asTransfer(transferDTO, bankAccount, OperationEnum.DEBIT, account)).thenReturn(transfer);
        when(this.transferRepository.save(transfer)).thenReturn(transfer);

        Transfer result = this.transferService.makeDebitTransfer(transferDTO, user.getMail());

        assertNotNull(result);
        assertEquals(transfer, result);
        assertEquals(500 - transfer.getAmount(), account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService).findBankAccountByUserMail(user.getMail());
        verify(transferMapper).asTransfer(transferDTO, bankAccount, OperationEnum.DEBIT, account);
        verify(transferRepository).save(transfer);
    }

    @Test
    @DisplayName("Should not make a debit transfer and not debit account -> InsufficientBalanceException")
    void shouldNotMakeDebitTransferInsufficientBalanceException() {

        account.setBalance(10);

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.of(account));


        assertThrows(InsufficientBalanceException.class, () -> this.transferService.makeDebitTransfer(transferDTO, user.getMail()));


        assertEquals(10, account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService, never()).findBankAccountByUserMail(any());
        verify(transferMapper, never()).asTransfer(any(), any(), any(), any());
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not make a debit transfer -> user account not found")
    void shouldNotMakeDebitTransferUserAccountNotFound() {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> this.transferService.makeDebitTransfer(transferDTO, user.getMail()));


        assertEquals(500, account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService, never()).findBankAccountByUserMail(any());
        verify(transferMapper, never()).asTransfer(any(), any(), any(), any());
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not make a debit transfer -> user bank account not found")
    void shouldNotMakeDebitTransferUserBankAccountNotFound() {

        when(this.accountService.findAccountByUserMail(user.getMail())).thenReturn(Optional.of(account));
        when(this.bankAccountService.findBankAccountByUserMail(user.getMail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> this.transferService.makeDebitTransfer(transferDTO, user.getMail()));

        assertEquals(500 - transfer.getAmount(), account.getBalance());

        verify(accountService).findAccountByUserMail(user.getMail());
        verify(bankAccountService).findBankAccountByUserMail(user.getMail());
        verify(transferMapper, never()).asTransfer(any(), any(), any(), any());
        verify(transferRepository, never()).save(any());
    }

}