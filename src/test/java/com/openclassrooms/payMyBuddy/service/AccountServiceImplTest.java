package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private Account userAccount;

    private AccountDTO userAccountDTO;

    private User user;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountMapper accountMapper;

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

        userAccountDTO = AccountDTO.builder()
                .balance(new BigDecimal(500))
                .build();
    }

    @Test
    @DisplayName("Should find account by user mail")
    void ShouldFindAccountByUserMail() {

        when(this.accountRepository.findByUserMail("john@test.com")).thenReturn(Optional.ofNullable(userAccount));

        Optional<Account> result = this.accountService.findAccountByUserMail("john@test.com");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(userAccount, result.get());

        verify(accountRepository).findByUserMail("john@test.com");
    }

    @Test
    @DisplayName("Should find accountDTO by user mail")
    void ShouldFindAccountDtoByUserMail() {

        when(this.accountRepository.findByUserMail("john@test.com")).thenReturn(Optional.ofNullable(userAccount));
        when(this.accountMapper.asAccountDTO(userAccount)).thenReturn(userAccountDTO);

        AccountDTO result = this.accountService.findAccountDtoByUserMail("john@test.com");

        assertNotNull(result);
        assertEquals(userAccountDTO, result);

        verify(accountRepository).findByUserMail("john@test.com");
        verify(accountMapper).asAccountDTO(userAccount);
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