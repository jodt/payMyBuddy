package com.openclassrooms.payMyBuddy.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotEmpty
    private String mail;

    @NotEmpty
    private String lastname;

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String password;

    private List<String> buddieEmail = new ArrayList<>();

}
