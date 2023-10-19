package com.openclassrooms.payMyBuddy.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@WebMvcTest(controllers = LoginController.class)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("Should display login form")
    void shouldShowLoginForm() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult.getResponse());
        assertEquals(mvcResult.getRequest().getRequestURI(), "/login");
    }
    
    @Test
    @DisplayName("user should be authenticated")
    void testUserLogin() throws Exception {
        mockMvc.perform(formLogin().user("user").password("1234")).andExpect(authenticated());
    }

    @Test
    @DisplayName("user should be unauthenticated")
    void testUserFailed() throws Exception {
        mockMvc.perform(formLogin().user("user").password("4567")).andExpect(unauthenticated());
    }

}