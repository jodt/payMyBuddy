package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BankAccountMapper;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.BankAccountRepository;
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
class BankAccountServiceImplTest {

    @Mock
    BankAccountRepository bankAccountRepository;

    @Mock
    UserService userService;

    @Mock
    BankAccountMapper bankAccountMapper;

    @InjectMocks
    BankAccountServiceImpl bankAccountService;

    private User user;

    private BankAccount bankAccount;

    private BankAccountDTO bankAccountDTO;

    @BeforeEach
    void init() {

        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        bankAccount = BankAccount.builder()
                .id(1)
                .user(user)
                .transfer(new ArrayList<>())
                .iban("FR12121212")
                .build();

        bankAccountDTO = BankAccountDTO.builder()
                .iban("FR12121212")
                .build();
    }

    @Test
    @DisplayName("Should find bank account with user's mail")
    void shouldFindBankAccountByUserMail() {

        when(this.bankAccountRepository.findByUserMail(user.getMail())).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> result = this.bankAccountService.findBankAccountByUserMail(user.getMail());

        assertTrue(result.isPresent());
        assertEquals(bankAccount, result.get());

        verify(bankAccountRepository).findByUserMail(user.getMail());
    }

    @Test
    @DisplayName("Should find bank account with user's mail and return a bankAccountDto")
    void shouldFindBankAccountDTOByUserMail() {

        when(this.bankAccountRepository.findByUserMail(user.getMail())).thenReturn(Optional.of(bankAccount));
        when(this.bankAccountMapper.asBankAccountDTO(bankAccount)).thenReturn(bankAccountDTO);

        BankAccountDTO result = this.bankAccountService.findBankAccountDTOByUserMail(user.getMail());

        assertNotNull(result);
        assertEquals(bankAccountDTO, result);

        verify(bankAccountRepository).findByUserMail(user.getMail());
        verify(bankAccountMapper).asBankAccountDTO(bankAccount);
    }

    @Test
    void save() {
    }

}