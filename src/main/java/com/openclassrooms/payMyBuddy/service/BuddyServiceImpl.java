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

    public List<BuddyDTO> getAllUsersAsBuddyDTO() throws ResourceNotFoundException {
        return this.userService.findAllOtherUsers().stream()
                .map(user -> this.buddyMapper.asBuddyDTO(user))
                .collect(Collectors.toList());
    }

    public User addBuddy(String mail) throws AlreadyBuddyExistException, ResourceNotFoundException {
        return this.userService.addBuddy(mail);
    }

}
