package com.openclassrooms.payMyBuddy.service;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.controller.mapper.BuddyMapper;
import com.openclassrooms.payMyBuddy.exceptions.AlreadyBuddyExistException;
import com.openclassrooms.payMyBuddy.exceptions.ResourceNotFoundException;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuddyServiceImpl implements BuddyService {

    private final UserService userService;

    private final BuddyMapper buddyMapper;


    public BuddyServiceImpl(UserService userService, BuddyMapper buddyMapper) {
        this.userService = userService;
        this.buddyMapper = buddyMapper;
    }

    /**
     * Method that retrieves all users except the user logged in as BuddyDTO
     *
     * @return a list of buddyDTO
     * @throws ResourceNotFoundException
     */
    public List<BuddyDTO> getAllUsersAsBuddyDTO() throws ResourceNotFoundException {
        return this.userService.findAllOtherUsers().stream()
                .map(user -> this.buddyMapper.asBuddyDTO(user))
                .collect(Collectors.toList());
    }

    /**
     * Method that takes a buddy's email and adds that buddy to the logged user's buddies list
     *
     * @param mail
     * @return The logged user whose buddies list was updated
     * @throws AlreadyBuddyExistException
     * @throws ResourceNotFoundException
     */
    public User addBuddy(String mail) throws AlreadyBuddyExistException, ResourceNotFoundException {
        return this.userService.addBuddy(mail);
    }

}
