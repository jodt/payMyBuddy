package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BankAccountDTO;
import com.openclassrooms.payMyBuddy.controller.dto.TransferDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.BankAccount;
import com.openclassrooms.payMyBuddy.model.Transfer;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.BankAccountService;
import com.openclassrooms.payMyBuddy.service.TransferService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(controllers = TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    TransferService transferService;

    private User user;

    private UserDTO userDTO;

    private TransferDTO transferDTO;

    private Transfer transfer;

    private Account account;

    private BankAccount bankAccount;

    @BeforeEach
    void init() {

        userDTO = UserDTO.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

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

        transfer = Transfer.builder()
                .account(account)
                .bankAccount(bankAccount)
                .amount(100)
                .build();
    }

    @Test
    @WithMockUser("john@test.com")
    void showTransferPage() throws Exception {

        mockMvc.perform(get("/transfer")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("debitTransfer", "creditTransfer"));
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should credit the account")
    void shouldCreditAccount() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(this.transferService.makeCreditTransfer(transferDTO, userDTO.getMail())).thenReturn(transfer);

        mockMvc.perform(post("/transfer/credit")
                        .with(csrf())
                        .flashAttr("creditTransfer", transferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("../home?creditsuccess"));

        verify(userService).getLoggedUserDTO();
        verify(transferService).makeCreditTransfer(transferDTO, userDTO.getMail());
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should not credit the account -> credit transfer has error field")
    void ShouldNotCreditAccount() throws Exception {


        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        mockMvc.perform(post("/transfer/credit")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("debitTransfer"))
                .andExpect(model().attributeHasFieldErrors("creditTransfer", "amount"));

        verify(userService).getLoggedUserDTO();
        verify(transferService, never()).makeCreditTransfer(transferDTO, userDTO.getMail());
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should debit the account")
    void shouldDebitAccount() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(this.transferService.makeDebitTransfer(transferDTO, userDTO.getMail())).thenReturn(transfer);

        mockMvc.perform(post("/transfer/debit")
                        .with(csrf())
                        .flashAttr("debitTransfer", transferDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("../home?debitsuccess"));

        verify(userService).getLoggedUserDTO();
        verify(transferService).makeDebitTransfer(transferDTO, userDTO.getMail());
    }


    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should not debit the account -> debit transfer has error field")
    void ShouldNotDebitAccount() throws Exception {


        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        mockMvc.perform(post("/transfer/debit")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("creditTransfer"))
                .andExpect(model().attributeHasFieldErrors("debitTransfer", "amount"));

        verify(userService).getLoggedUserDTO();
        verify(transferService, never()).makeCreditTransfer(transferDTO, userDTO.getMail());
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should not debit the account -> InsufficientBalanceException")
    void ShouldNotDebitAccountInsufficientBalanceException() throws Exception {


        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(this.transferService.makeDebitTransfer(transferDTO, userDTO.getMail())).thenThrow(InsufficientBalanceException.class);

        mockMvc.perform(post("/transfer/debit")
                        .with(csrf())
                        .flashAttr("debitTransfer", transferDTO))
                .andExpect(redirectedUrl("../home?debiterror"));

        verify(userService).getLoggedUserDTO();
        verify(transferService).makeDebitTransfer(transferDTO, userDTO.getMail());
    }

}