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

    /**
     * Method that returns a list of all users except the logged user
     *
     * @return a list of users
     * @throws ResourceNotFoundException
     */
    @Override
    public List<User> findAllOtherUsers() throws ResourceNotFoundException {
        String loggedUserMail = this.getLoggedUser().getMail();
        log.info("Recovery of all users other than the connected user {}", loggedUserMail);
        return this.userRepository.findAll().stream()
                .filter(user -> !user.getMail().equals(loggedUserMail))
                .collect(Collectors.toList());
    }

    /**
     * Method that save a user in the database
     *
     * @param user
     * @return the saved user
     * @throws UserAlreadyExistException
     */
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

        this.createUserAccount(userSaved);
        return userSaved;
    }

    @Override
    public Optional<User> findByMail(String mail) {
        return this.userRepository.findByMail(mail);
    }

    /**
     * Method that returns the logged user
     *
     * @return User logged
     * @throws ResourceNotFoundException
     */
    @Override
    public User getLoggedUser() throws ResourceNotFoundException {
        log.info("Try to find logged user");
        String loggedUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.userRepository.findByMail(loggedUserMail).orElseThrow(() -> new ResourceNotFoundException());
    }

    /**
     * Method that return the logged user as userDTO
     *
     * @return UserDTO
     * @throws ResourceNotFoundException
     */
    @Override
    public UserDTO getLoggedUserDTO() throws ResourceNotFoundException {
        log.info("Try to retrieve logged userDTO");
        return this.userMapper.asUserDTO(this.getLoggedUser());
    }

    /**
     * Method that takes a buddy's email and adds that buddy to the logged user's buddies list
     *
     * @param buddyMail
     * @return The logged user whose buddies list was updated
     * @throws AlreadyBuddyExistException if the buddy already exist in the user's buddies list
     * @throws ResourceNotFoundException  if the user is not found
     */
    @Override
    public User addBuddy(String buddyMail) throws AlreadyBuddyExistException, ResourceNotFoundException {
        User user = this.getLoggedUser();
        Optional<User> buddytoAdd = this.userRepository.findByMail(buddyMail);
        if (buddytoAdd.isPresent()) {
            if (checkIfBuddyAlreadyExist(user, buddytoAdd.get())) {
                log.error("Buddy already added");
                throw new AlreadyBuddyExistException();
            }
            user.getBuddies().add(buddytoAdd.get());
            this.userRepository.save(user);
            log.info("Buddy successfully added");
        }
        return user;
    }

    /**
     * Method that takes a user and creates the user account with a zero balance
     * and a unique identifier. This method is used during user registration
     *
     * @param userSaved
     */
    private void createUserAccount(User userSaved) {
        Account userAccount = Account.builder()
                .number(RandomAccountNumber.createAccountNumber())
                .balance(0)
                .user(userSaved)
                .build();

        this.accountRepository.save(userAccount);
    }

    /**
     * Method used to check if a buddy already exists in the user's buddies list
     *
     * @param user
     * @param Buddy
     * @return return true if the friend already exists in the list otherwise false
     */
    private boolean checkIfBuddyAlreadyExist(User user, User Buddy) {
        return user.getBuddies().contains(Buddy);
    }

}
