package com.openclassrooms.payMyBuddy.Controller.mapper;

import com.openclassrooms.payMyBuddy.Controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public User asUser(UserDTO userDTO) {
        return User.builder()
                .firstname(userDTO.getFirstname())
                .lastname(userDTO.getLastname())
                .mail(userDTO.getMail())
                .password(userDTO.getPassword())
                .build();
    }

    @Override
    public UserDTO asUserDTO(User user) {
        return UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .mail(user.getMail())
                .buddieEmail(user.getBuddies().stream().map(buddy -> buddy.getMail()).collect(Collectors.toList()))
                .build();
    }


}
