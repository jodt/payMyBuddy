package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegisterController.class)
class RegisterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private User user;

    private UserDTO userDTO;

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

    }

    @Test
    @WithMockUser("john@test.com")
    void shouldShowRegisterForm() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser("john@test.com")
    void shouldSubmitRegisterForm() throws Exception {

        when(this.userService.saveUser(userDTO)).thenReturn(user);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("user", userDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login?registerSuccess"));

    }

    @Test
    @WithMockUser("john@test.com")
    void shouldNotSubmitRegisterForm() throws Exception {

        when(this.userService.saveUser(any(UserDTO.class))).thenThrow(UserAlreadyExistException.class);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("user", userDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userAlreadyExistError"))
                .andExpect(model().attribute("userAlreadyExistError", "Un compte est déjà associé à cet email"));
    }

}