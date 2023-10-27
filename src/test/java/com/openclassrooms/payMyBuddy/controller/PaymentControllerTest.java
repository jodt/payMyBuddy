package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.PaymentDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.InsufficientBalanceException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.Payment;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.PaymentService;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    PaymentService paymentService;

    @MockBean
    UserMapper userMapper;

    private User user;

    private UserDTO userDTO;

    private Payment payment;

    private Account issuerAccount;

    private Account receiverAccount;

    private PaymentDTO paymentDTO;

    @BeforeEach
    void init() {

        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        userDTO = UserDTO.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        issuerAccount = Account.builder()
                .number("123456789")
                .balance(500)
                .user(user)
                .id(1)
                .build();

        receiverAccount = Account.builder()
                .number("987654321")
                .balance(200)
                .user(User.builder()
                        .mail("receiver@gmail.com")
                        .build())
                .id(2)
                .build();

        payment = Payment.builder()
                .amount(100)
                .description("remboursement")
                .issuerAccount(issuerAccount)
                .receiverAccount(receiverAccount)
                .build();

        paymentDTO = PaymentDTO.builder()
                .amount(new BigDecimal(100))
                .description("remboursement")
                .receiverMail("receiver@gmail.com")
                .build();
    }


    @Test
    @WithMockUser("john@test.com")
    void shouldGetPayments() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        when(this.paymentService.getAllPayments(userDTO, 0, 3)).thenReturn(new PageImpl<>(List.of(paymentDTO)));

        mockMvc.perform(get("/payment")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attributeExists("user", "payment", "payments", "pages", "currentPage"))
                .andExpect(model().attribute("user", equalTo(userDTO)))
                .andExpect(model().attribute("payments", hasSize(1)))
                .andExpect(model().attribute("payments", allOf(hasItem(paymentDTO))))
                .andExpect(model().attribute("pages", equalTo(new int[1])))
                .andExpect(model().attribute("currentPage", equalTo(0)));

        verify(userService).getLoggedUserDTO();
        verify(paymentService).getAllPayments(userDTO, 0, 3);

    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("Should make payment")
    void shouldMakePayment() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        when(this.paymentService.makePayment(paymentDTO, userDTO)).thenReturn(payment);

        mockMvc.perform(post("/payment")
                        .with(csrf())
                        .param("page", "0")
                        .flashAttr("payment", paymentDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment?page=0&success"));

    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("should not make paiement -> validation payment error")
    void shouldNotMakePayment() throws Exception {

        PaymentDTO errorPayment = PaymentDTO.builder()
                .amount(new BigDecimal(100))
                .description("remboursement")
                .build();

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        when(this.paymentService.getAllPayments(userDTO, 0, 3)).thenReturn(new PageImpl<>(List.of(paymentDTO)));


        mockMvc.perform(post("/payment")
                        .with(csrf())
                        .param("page", "0")
                        .flashAttr("payment", errorPayment))
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attribute("payments", hasSize(1)))
                .andExpect(model().attribute("pages", new int[1]))
                .andExpect(model().attribute("currentPage", 0));
        ;

    }

    @Test
    @WithMockUser("john@test.com")
    @DisplayName("should not make paiement -> insufficiant balance exception")
    void shouldNotMakePaymentInsufficientBalanceException() throws Exception {

        when(this.userService.getLoggedUserDTO()).thenReturn(userDTO);

        when(this.paymentService.getAllPayments(userDTO, 0, 3)).thenReturn(new PageImpl<>(List.of(paymentDTO)));

        when(this.paymentService.makePayment(ArgumentMatchers.any(PaymentDTO.class), ArgumentMatchers.any(UserDTO.class))).thenThrow(InsufficientBalanceException.class);


        mockMvc.perform(post("/payment")
                        .with(csrf())
                        .flashAttr("payment", paymentDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("payment?balanceError"));

    }

}