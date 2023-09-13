package com.openclassrooms.payMyBuddy.Controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty
    private String mail;

    @NotEmpty
    private String lastname;

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String password;

    private List<UserDTO> buddies;

}
