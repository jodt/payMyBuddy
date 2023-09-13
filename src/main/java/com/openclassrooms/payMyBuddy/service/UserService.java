package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.User;

import java.util.Optional;

public interface UserService {

    public Iterable<User> findAllUsers();

    public User saveUser(UserDTO user);

    public Optional<User> findByMail(String mail);

}
