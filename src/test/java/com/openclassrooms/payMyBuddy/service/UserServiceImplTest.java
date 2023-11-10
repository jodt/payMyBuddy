package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserDTO userDTO;

    private User user;

    private User user2;

    private Account account;

    @Mock
    UserRepository userRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    Authentication authentication;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<String> mailCaptor;


    @BeforeEach
    public void init() {

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
                .buddies(new ArrayList<>())
                .build();

        user2 = User.builder()
                .lastname("DUMONT")
                .firstname("sophie")
                .mail("sophie@test.com")
                .password("1234")
                .build();

        account = Account.builder()
                .id(1)
                .number("123456789")
                .balance(100)
                .build();

    }

    @Test
    @DisplayName("Should save user")
    void shouldSaveUser() throws UserAlreadyExistException {

        when(this.userRepository.findByMail(anyString())).thenReturn(Optional.empty());
        when(this.userMapper.asUser(userDTO)).thenReturn(user);
        when(this.passwordEncoder.encode("1234")).thenReturn("ABCD");
        when(this.userRepository.save(user)).thenReturn(user);
        when(this.accountRepository.save(any(Account.class))).thenReturn(account);

        User userResult = this.userService.saveUser(userDTO);

        verify(this.userRepository).findByMail(mailCaptor.capture());
        verify(this.userMapper).asUser(userDTO);
        verify(this.passwordEncoder).encode("1234");
        verify(this.userRepository).save(user);
        verify(this.accountRepository).save(any(Account.class));

        String mail = mailCaptor.getValue();

        assertEquals("john@test.com", mail);
        assertEquals(user, userResult);
    }

    @Test
    @DisplayName("Should not saved user -> user already exist")
    void shouldNotSaveUser() {
        when(this.userRepository.findByMail(anyString())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistException.class, () -> this.userService.saveUser(userDTO));
    }

    @Test
    @DisplayName("Should find user by mail")
    void shouldFindByMail() {

        when(this.userRepository.findByMail("john@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = this.userService.findByMail("john@test.com");

        assertNotNull(result);
        assertEquals(user, result.get());

        verify(userRepository).findByMail("john@test.com");

    }


    @Test
    @DisplayName("Should return all users without the logged user")
    void shouldFindAllOtherUsers() throws ResourceNotFoundException {

        when(authentication.getName()).thenReturn("john@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(this.userRepository.findByMail("john@test.com")).thenReturn(Optional.ofNullable(user));
        when(this.userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> result = this.userService.findAllOtherUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(!result.contains(user));
        assertTrue(result.contains(user2));

        verify(authentication, times(1)).getName();
        verify(userRepository, times(1)).findByMail("john@test.com");
        verify(userRepository).findAll();
    }


    @Test
    @DisplayName("Should add a buddy")
    void shouldAddBuddy() throws AlreadyBuddyExistException, ResourceNotFoundException {
        when(authentication.getName()).thenReturn("john@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(this.userRepository.findByMail("john@test.com")).thenReturn(Optional.ofNullable(user));
        when(this.userRepository.findByMail("sophie@test.com")).thenReturn(Optional.ofNullable(user2));
        when(this.userRepository.save(user)).thenReturn(user);

        User result = this.userService.addBuddy("sophie@test.com");

        assertNotNull(result);
        assertEquals(user, result);
        assertEquals(1, result.getBuddies().size());
        assertEquals(user2, result.getBuddies().get(0));

        verify(userRepository).findByMail("john@test.com");
        verify(userRepository).findByMail("sophie@test.com");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should not add buddy -> buddy already exist")
    void shouldNotAddBuddy() {
        user.getBuddies().add(user2);

        when(authentication.getName()).thenReturn("john@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(this.userRepository.findByMail("john@test.com")).thenReturn(Optional.ofNullable(user));
        when(this.userRepository.findByMail("sophie@test.com")).thenReturn(Optional.ofNullable(user2));

        assertThrows(AlreadyBuddyExistException.class, () -> this.userService.addBuddy("sophie@test.com"));

        verify(userRepository).findByMail("john@test.com");
        verify(userRepository).findByMail("sophie@test.com");
        verify(userRepository, never()).save(user);

    }

    @Test
    void shouldGetLoggedUser() throws ResourceNotFoundException {

        when(authentication.getName()).thenReturn("john@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(this.userRepository.findByMail("john@test.com")).thenReturn(Optional.of(user));
        User result = this.userService.getLoggedUser();

        assertEquals(user, result);

        verify(userRepository).findByMail("john@test.com");
    }

}