package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.User;

public interface UserMapper {

    public User asUser(UserDTO userDTO);

}
