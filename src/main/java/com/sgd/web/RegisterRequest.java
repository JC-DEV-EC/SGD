package com.sgd.web;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String firebaseUid,
        @NotBlank String email,
        @NotBlank String nombre
) {}
