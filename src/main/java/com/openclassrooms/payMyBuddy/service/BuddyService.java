package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.User;

import java.util.List;

public interface BuddyService {

    public List<BuddyDTO> getAllUsersAsBuddyDTO() throws ResourceNotFoundException;

    public User addBuddy(String mail) throws AlreadyBuddyExistException, ResourceNotFoundException;

}
