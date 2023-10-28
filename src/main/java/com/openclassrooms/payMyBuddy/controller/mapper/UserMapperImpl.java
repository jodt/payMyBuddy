package com.openclassrooms.payMyBuddy.controller.mapper;

import com.openclassrooms.payMyBuddy.controller.dto.UserDTO;
import com.openclassrooms.payMyBuddy.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
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
        log.info("map user to userDTO");
        return UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .mail(user.getMail())
                .buddieEmail(user.getBuddies().stream().map(buddy -> buddy.getMail()).collect(Collectors.toList()))
                .build();
    }


}
