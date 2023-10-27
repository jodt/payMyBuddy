package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private Account userAccount;

    private User user;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountServiceImpl accountService;

    @BeforeEach
    void init() {

        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .buddies(new ArrayList<>())
                .build();

        userAccount = Account.builder()
                .user(user)
                .balance(500)
                .number("123456789")
                .build();
    }

    @Test
    @DisplayName("Should find account by user mail")
    void ShouldFindByUserMail() {

        when(this.accountRepository.findByUserMail("john@test.com")).thenReturn(Optional.ofNullable(userAccount));

        Optional<Account> result = this.accountService.findAccountByUserMail("john@test.com");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(userAccount, result.get());

        verify(accountRepository).findByUserMail("john@test.com");


    }

    @Test
    @DisplayName("Should save a account")
    void shouldSaveAccount() {

        when(accountRepository.save(userAccount)).thenReturn(userAccount);

        Account result = this.accountService.saveAccount(userAccount);

        assertNotNull(result);
        assertEquals(userAccount, result);

        verify(accountRepository).save(userAccount);
    }

}