package com.sgd.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApproval, Long> {

    Optional<UserApproval> findByFirebaseUid(String firebaseUid);

    List<UserApproval> findByStatus(String status);
}
