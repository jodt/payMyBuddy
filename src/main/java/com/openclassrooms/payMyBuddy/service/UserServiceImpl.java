package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.Controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.utils.RandomAccountNumber;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private AccountRepository accountRepository;

    private UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<User> findAllUsers() {
        return this.userRepository.findAll(PageRequest.of(0, 5));
    }

    @Override
    @Transactional
    public User saveUser(UserDTO user) throws UserAlreadyExistException {

        Optional<User> existingUser = this.userRepository.findByMail(user.getMail());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException("Un compte est déjà associé à cet email");
        }
        User userToSave = userMapper.asUser(user);

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


}
