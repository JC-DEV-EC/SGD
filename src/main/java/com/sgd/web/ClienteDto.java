package com.sgd.web;

import com.sgd.domain.Cliente;

import java.math.BigDecimal;

public record ClienteDto(
        Long id,
        String fullName,
        String city,
        String registrationDate,
        BigDecimal debt,
        BigDecimal payment,
        BigDecimal totalAmount,
        Boolean discount,
        String status
) {
    public static ClienteDto fromEntity(Cliente c) {
        String name = (c.getFirstName() + " " + c.getLastName()).trim();
        String date = c.getRegistrationDate() != null ? c.getRegistrationDate().toString() : null;
        return new ClienteDto(
                c.getId(),
                name,
                c.getCity(),
                date,
                c.getDebt(),
                c.getPayment(),
                c.getTotalAmount(),
                c.getDiscount(),
                c.getStatus()
        );
    }
}
