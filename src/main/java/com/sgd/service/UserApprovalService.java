package com.sgd.service;

import com.sgd.domain.UserApproval;
import com.sgd.domain.UserApprovalRepository;
import com.sgd.web.RegisterRequest;
import com.sgd.web.UserApprovalDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserApprovalService {

    private static final Logger log = LoggerFactory.getLogger(UserApprovalService.class);

    private final UserApprovalRepository repository;

    public UserApprovalService(UserApprovalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserApprovalDto register(RegisterRequest request) {
        // Check if already registered
        Optional<UserApproval> existing = repository.findByFirebaseUid(request.firebaseUid());
        if (existing.isPresent()) {
            return UserApprovalDto.fromEntity(existing.get());
        }

        UserApproval entity = new UserApproval();
        entity.setFirebaseUid(request.firebaseUid());
        entity.setEmail(request.email());
        entity.setNombre(request.nombre());
        entity.setStatus("PENDING");
        entity.setRole("USER");

        UserApproval saved = repository.save(entity);
        log.info("Registered user firebaseUid={} email={}", saved.getFirebaseUid(), saved.getEmail());
        return UserApprovalDto.fromEntity(saved);
    }

    public Optional<UserApprovalDto> getStatus(String firebaseUid) {
        return repository.findByFirebaseUid(firebaseUid)
                .map(UserApprovalDto::fromEntity);
    }

    public List<UserApprovalDto> getPending() {
        return repository.findByStatus("PENDING").stream()
                .map(UserApprovalDto::fromEntity)
                .toList();
    }

    @Transactional
    public Optional<UserApprovalDto> approve(Long id, String adminUid) {
        // Verify admin
        Optional<UserApproval> admin = repository.findByFirebaseUid(adminUid);
        if (admin.isEmpty() || !"ADMIN".equals(admin.get().getRole())) {
            return Optional.empty();
        }

        return repository.findById(id).map(user -> {
            user.setStatus("APPROVED");
            user.setApprovedBy(adminUid);
            user.setApprovedAt(OffsetDateTime.now());
            UserApproval saved = repository.save(user);
            log.info("Approved user id={} by admin={}", id, adminUid);
            return UserApprovalDto.fromEntity(saved);
        });
    }

    @Transactional
    public Optional<UserApprovalDto> reject(Long id, String adminUid) {
        Optional<UserApproval> admin = repository.findByFirebaseUid(adminUid);
        if (admin.isEmpty() || !"ADMIN".equals(admin.get().getRole())) {
            return Optional.empty();
        }

        return repository.findById(id).map(user -> {
            user.setStatus("REJECTED");
            user.setApprovedBy(adminUid);
            user.setApprovedAt(OffsetDateTime.now());
            UserApproval saved = repository.save(user);
            log.info("Rejected user id={} by admin={}", id, adminUid);
            return UserApprovalDto.fromEntity(saved);
        });
    }

    public List<UserApprovalDto> getAllUsers(String adminUid) {
        if (!isAdmin(adminUid)) return List.of();
        return repository.findAll().stream()
                .map(UserApprovalDto::fromEntity)
                .toList();
    }

    @Transactional
    public Optional<UserApprovalDto> changeRole(Long id, String newRole, String adminUid) {
        if (!isAdmin(adminUid)) return Optional.empty();
        return repository.findById(id).map(user -> {
            user.setRole(newRole);
            UserApproval saved = repository.save(user);
            log.info("Changed role of user id={} to {} by admin={}", id, newRole, adminUid);
            return UserApprovalDto.fromEntity(saved);
        });
    }

    @Transactional
    public boolean deleteUser(Long id, String adminUid) {
        if (!isAdmin(adminUid)) return false;
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Deleted user id={} by admin={}", id, adminUid);
            return true;
        }
        return false;
    }

    private boolean isAdmin(String uid) {
        return repository.findByFirebaseUid(uid)
                .map(u -> "ADMIN".equals(u.getRole()))
                .orElse(false);
    }
}
