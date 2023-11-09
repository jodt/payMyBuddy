package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.UserNotFoundException;
import com.openclassrooms.payMyBuddy.model.User;

import java.util.List;

public interface BuddyService {

    public List<BuddyDTO> getAllUsersAsBuddyDTO() throws UserNotFoundException;

    public User addBuddy(String mail) throws AlreadyBuddyExistException, UserNotFoundException;

}
