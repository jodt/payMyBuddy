package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.UserMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.model.Account;
import com.openclassrooms.payMyBuddy.model.User;
import com.openclassrooms.payMyBuddy.repository.AccountRepository;
import com.openclassrooms.payMyBuddy.repository.UserRepository;
import com.openclassrooms.payMyBuddy.utils.RandomAccountNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    public List<User> findAllOtherUsers() {
        return this.userRepository.findAll().stream()
                .filter(user -> !user.getMail().equals(this.getLoggedUser().getMail()))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
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
    public User getLoggedUser() {
        log.info("Try to find logged user");
        String loggedUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.userRepository.findByMail(loggedUserMail).get();
    }

    @Override
    public UserDTO getLoggedUserDTO() {
        log.info("Try to retrieve logged userDTO");
        return this.userMapper.asUserDTO(this.getLoggedUser());
    }

    @Override
    public User addBuddy(String buddyMail) throws AlreadyBuddyExistException {
        User user = this.getLoggedUser();
        Optional<User> buddytoAdd = this.userRepository.findByMail(buddyMail);
        if (buddytoAdd.isPresent()) {
            if (checkIfBuddyAlreadyExist(user, buddytoAdd.get())) {
                throw new AlreadyBuddyExistException();
            }
            user.getBuddies().add(buddytoAdd.get());
            this.userRepository.save(user);
        }
        return user;
    }

    private boolean checkIfBuddyAlreadyExist(User user, User Buddy) {
        return user.getBuddies().contains(Buddy);
    }

}
