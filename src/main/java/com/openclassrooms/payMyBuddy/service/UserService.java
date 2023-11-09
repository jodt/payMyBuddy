package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.UserAlreadyExistException;
import com.openclassrooms.payMyBuddy.exceptions.UserNotFoundException;
import com.openclassrooms.payMyBuddy.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public List<User> findAllOtherUsers() throws UserNotFoundException;

    public User saveUser(UserDTO user) throws UserAlreadyExistException;

    public Optional<User> findByMail(String mail);

    public User getLoggedUser() throws UserNotFoundException;

    public UserDTO getLoggedUserDTO() throws UserNotFoundException;

    public User addBuddy(String buddyMail) throws AlreadyBuddyExistException, UserNotFoundException;

}
