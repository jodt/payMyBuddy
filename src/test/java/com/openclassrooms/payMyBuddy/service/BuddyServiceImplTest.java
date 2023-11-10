package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BuddyMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuddyServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    BuddyMapper buddyMapper;

    @InjectMocks
    BuddyServiceImpl buddyService;

    private User user;

    private User user2;

    private BuddyDTO buddyDTO;

    @BeforeEach
    void init() {

        user = User.builder()
                .lastname("DUMONT")
                .firstname("john")
                .mail("john@test.com")
                .password("1234")
                .buddies(new ArrayList<>())
                .build();

        user2 = User.builder()
                .lastname("DUMOULIN")
                .firstname("pierre")
                .mail("pierre@test.com")
                .password("1234")
                .buddies(new ArrayList<>())
                .build();

        buddyDTO = BuddyDTO.builder()
                .buddyLastName("DUMOULIN")
                .buddyFirstName("pierre")
                .buddyEmail("pierre@test.com")
                .build();
    }

    @Test
    @DisplayName("Should get all users as buddyDTO")
    void shouldGetAllUsersAsBuddyDTO() throws ResourceNotFoundException {

        when(this.userService.findAllOtherUsers()).thenReturn(List.of(user2));
        when(this.buddyMapper.asBuddyDTO(user2)).thenReturn(buddyDTO);

        List<BuddyDTO> result = this.buddyService.getAllUsersAsBuddyDTO();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(buddyDTO, result.get(0));

        verify(userService).findAllOtherUsers();
        verify(buddyMapper).asBuddyDTO(user2);
    }

    @Test
    @DisplayName("Should add buddy")
    void addBuddy() throws AlreadyBuddyExistException, ResourceNotFoundException {

        user.getBuddies().add(user2);

        when(this.userService.addBuddy("pierre@test.com")).thenReturn(user);

        User result = this.buddyService.addBuddy("pierre@test.com");

        assertNotNull(result);
        assertEquals(user, result);
        assertEquals(1, result.getBuddies().size());

        verify(userService).addBuddy("pierre@test.com");
    }

}