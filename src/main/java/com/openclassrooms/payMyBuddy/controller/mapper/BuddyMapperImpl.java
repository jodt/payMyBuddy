package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.stereotype.Service;

@Service
public class BuddyMapperImpl implements BuddyMapper {

    public BuddyDTO asBuddyDTO(User user) {
        return BuddyDTO.builder()
                .buddyEmail(user.getMail())
                .buddyLastName(user.getLastname())
                .buddyFirstName(user.getFirstname())
                .build();
    }

}
