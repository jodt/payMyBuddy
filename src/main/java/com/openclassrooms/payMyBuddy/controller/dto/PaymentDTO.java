package com.openclassrooms.payMyBuddy.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    @NotEmpty
    private String receiverMail;

    @Min(value = 1)
    private BigDecimal amount;

    @NotEmpty
    private String description;


}
