package com.openclassrooms.payMyBuddy.controller.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferDTO {

    @NotNull
    @DecimalMin(value = "1.00", inclusive = false)
    private BigDecimal amount;

}
