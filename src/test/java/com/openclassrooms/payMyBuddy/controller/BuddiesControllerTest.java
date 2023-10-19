package com.openclassrooms.payMyBuddy.controller;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BuddyMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.service.BuddyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BuddiesController.class)
class BuddiesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BuddyService buddyService;

    @MockBean
    BuddyMapper buddyMapper;

    private BuddyDTO buddyDTO;

    @BeforeEach
    void init() {

        buddyDTO = BuddyDTO.builder()
                .buddyEmail("john@test.com")
                .buddyFirstName("john")
                .buddyLastName("DUMONT")
                .build();
    }


    @WithMockUser("john@test.com")
    @Test
    @DisplayName("Should display add buddy from")
    void shouldShowAddBuddyForm() throws Exception {

        mockMvc.perform(get("/buddies"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users", "buddyToAdd"))
                .andExpect(view().name("buddies"));
    }

    @Test
    @WithMockUser("john@test.com")
    void shouldAddBuddy() throws Exception {

        mockMvc.perform(post("/buddies")
                        .with(csrf())
                        .flashAttr("buddyToAdd", buddyDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("payment?buddyAdded"));
    }

    @Test
    @WithMockUser("john@test.com")
    void shouldNotAddBuddyAlreadyBuddyExistException() throws Exception {

        when(this.buddyService.addBuddy(anyString())).thenThrow(AlreadyBuddyExistException.class);

        mockMvc.perform(post("/buddies")
                        .with(csrf())
                        .flashAttr("buddyToAdd", buddyDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("buddies"))
                .andExpect(model().attributeExists("users", "buddyToAdd", "AlreadyBuddyExistException"));

    }

    @Test
    @WithMockUser("john@test.com")
    void shouldNotAddBuddyFormError() throws Exception {


        mockMvc.perform(post("/buddies")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("buddies"))
                .andExpect(model().attributeHasFieldErrors("buddyToAdd", "buddyEmail"));

    }

}