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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfilController.class)
class ProfilControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    BankAccountService bankAccountService;

    @MockBean
    TransferService transferService;

    private User user;

    private UserDTO userDTO;

    private BankAccountDTO bankAccountDTO;

    private BankAccount bankAccount;

    private Account account;

    private TransferDTO transferDTO;

    private Transfer transfer;

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

        account = Account.builder()
                .number("123456789")
                .balance(500)
                .user(user)
                .id(1)
                .build();

        bankAccountDTO = BankAccountDTO.builder()
                .iban("FR12121212")
                .build();

        bankAccount = BankAccount.builder()
                .id(1)
                .user(user)
                .transfer(new ArrayList<>())
                .iban("FR12121212")
                .build();


    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should display profile information")
    void shouldGetProfil() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(this.bankAccountService.findBankAccountDTOByUserMail(userDTO.getMail())).thenReturn(bankAccountDTO);

        mockMvc.perform(get("/profil")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attribute("userBankAccount", hasProperty("iban", equalTo("FR12121212"))))
                .andExpect(model().attribute("loggedUser", userDTO));

        verify(this.userService).getLoggedUserDTO();
        verify(this.bankAccountService).findBankAccountDTOByUserMail(userDTO.getMail());
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should save user bank account")
    void shouldSaveUserBankAccount() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);
        when(this.bankAccountService.save(bankAccountDTO, userDTO.getMail())).thenReturn(bankAccount);

        mockMvc.perform(post("/profil")
                        .with(csrf())
                        .flashAttr("userBankAccount", bankAccountDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("profil"));
    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should not save user bank account -> iban field has error")
    void shouldNotSaveUserBankAccount() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        mockMvc.perform(post("/profil")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("profil?ibanError"));
    }

}