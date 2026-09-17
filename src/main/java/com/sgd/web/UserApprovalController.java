package com.sgd.web;

import com.sgd.service.UserApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserApprovalController {

    private final UserApprovalService service;

    public UserApprovalController(UserApprovalService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserApprovalDto> register(@Valid @RequestBody RegisterRequest request) {
        UserApprovalDto dto = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/status/{firebaseUid}")
    public ResponseEntity<UserApprovalDto> getStatus(@PathVariable String firebaseUid) {
        return service.getStatus(firebaseUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public List<UserApprovalDto> getPending(@RequestHeader("X-Admin-Uid") String adminUid) {
        return service.getPending();
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<UserApprovalDto> approve(@PathVariable Long id,
                                                    @RequestHeader("X-Admin-Uid") String adminUid) {
        return service.approve(id, adminUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<UserApprovalDto> reject(@PathVariable Long id,
                                                   @RequestHeader("X-Admin-Uid") String adminUid) {
        return service.reject(id, adminUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/users")
    public List<UserApprovalDto> getAllUsers(@RequestHeader("X-Admin-Uid") String adminUid) {
        return service.getAllUsers(adminUid);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserApprovalDto> changeRole(@PathVariable Long id,
                                                      @RequestParam String role,
                                                      @RequestHeader("X-Admin-Uid") String adminUid) {
        return service.changeRole(id, role, adminUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestHeader("X-Admin-Uid") String adminUid) {
        if (service.deleteUser(id, adminUid)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
