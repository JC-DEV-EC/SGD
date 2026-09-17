package com.sgd.web;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ClientPaymentRequest(
        @NotNull BigDecimal amount,
        String reference
) {
}
