package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.utils.RandomAccountNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAllUsers() {
        return this.userRepository.findAll(PageRequest.of(0, 5));
    }

    @Override
    public User saveUser(UserDTO user) throws UserAlreadyExistException {

        Optional<User> existingUser = this.userRepository.findByMail(user.getMail());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException("Un compte est déjà associé à cet email");
        }
        
        User userToSave = userMapper.asUser(user);
        userToSave.setPassword(this.passwordEncoder.encode(userToSave.getPassword()));
        User userSaved = this.userRepository.save(userToSave);

        Account userAccount = Account.builder()
                .number(RandomAccountNumber.createAccountNumber())
                .balance(0)
                .user(userSaved)
                .build();

        this.accountRepository.save(userAccount);
        return userSaved;
    }

    @Override
    public Optional<User> findByMail(String mail) {
        return this.userRepository.findByMail(mail);
    }

    @Override
    public UserDTO getLoggedUser() {
        String loggedUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = this.userRepository.findByMail(loggedUserMail).get();
        return this.userMapper.asUserDTO(loggedUser);
    }

}
