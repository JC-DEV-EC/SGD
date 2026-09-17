package com.sgd.web;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ClientChargeRequest(
        @NotNull BigDecimal amount,
        String description
) {
}
