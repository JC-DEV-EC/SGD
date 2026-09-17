package com.sgd.web;

import com.sgd.domain.UserApproval;

public record UserApprovalDto(
        Long id,
        String firebaseUid,
        String email,
        String nombre,
        String status,
        String role,
        String createdAt
) {
    public static UserApprovalDto fromEntity(UserApproval u) {
        return new UserApprovalDto(
                u.getId(),
                u.getFirebaseUid(),
                u.getEmail(),
                u.getNombre(),
                u.getStatus(),
                u.getRole(),
                u.getCreatedAt() != null ? u.getCreatedAt().toString() : null
        );
    }
}
