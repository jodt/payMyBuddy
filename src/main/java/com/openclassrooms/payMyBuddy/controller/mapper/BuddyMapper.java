package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.BuddyDTO;
import com.openclassrooms.payMyBuddy.model.User;

public interface BuddyMapper {

    BuddyDTO asBuddyDTO(User user);

}
