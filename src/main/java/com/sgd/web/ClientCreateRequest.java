package com.sgd.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClientCreateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String city,
        LocalDateTime registrationDate,
        BigDecimal initialDebt,
        Boolean discount
) {
}
