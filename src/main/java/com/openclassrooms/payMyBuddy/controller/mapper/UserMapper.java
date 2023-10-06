package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.User;

public interface UserMapper {

    User asUser(UserDTO userDTO);

    UserDTO asUserDTO(User user);


}
