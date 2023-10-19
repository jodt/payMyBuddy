package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.AccountDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.AccountMapper;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    AccountMapper accountMapper;

    @MockBean
    Authentication authentication;


    @Test
    @WithMockUser("john@test.com")
    void shouldShowHomePage() throws Exception {

        User user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .build();

        Account userAccount = Account.builder()
                .user(user)
                .build();

        AccountDTO accountDto = AccountDTO.builder()
                .balance(new BigDecimal(500))
                .build();

        when(this.accountService.findByUserMail(anyString())).thenReturn(Optional.of(userAccount));
        when(this.accountMapper.asAccountDTO(userAccount)).thenReturn(accountDto);

        this.mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("account", "date"))
                .andExpect(model().attribute("date", (LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))))
                .andExpect(model().attribute("account", hasProperty("balance", equalTo(BigDecimal.valueOf(500)))));
    }

}