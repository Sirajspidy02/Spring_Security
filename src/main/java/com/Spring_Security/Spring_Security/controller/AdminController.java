package com.Spring_Security.Spring_Security.controller;


import com.Spring_Security.Spring_Security.dto.response.UserResponse;
import com.Spring_Security.Spring_Security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users/{userId}/lock")
    public ResponseEntity<String> lockUser(@PathVariable Long userId) {
        userService.lockUser(userId);
        return ResponseEntity.ok("User locked successfully");
    }

    @PostMapping("/users/{userId}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return ResponseEntity.ok("User unlocked successfully");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
