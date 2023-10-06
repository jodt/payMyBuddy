package com.openclassrooms.payMyBuddy.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuddyDTO {

    @NotEmpty
    private String buddyEmail;

    private String buddyLastName;

    private String buddyFirstName;

}
