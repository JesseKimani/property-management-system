package com.example.pms_v1.controllers;

import com.example.pms_v1.dto.AuthDto;
import com.example.pms_v1.models.User;
import com.example.pms_v1.models.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.pms_v1.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) Long companyId) {
        List<User> users = (companyId != null)
                ? userService.getUsersByRoleAndCompany(RoleType.USER.name(), companyId)
                : userService.getUsersByRole(RoleType.USER.name());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody AuthDto.UserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/view-tenants")
    public String viewTenants(@RequestBody User user) {
        return "List of tenants";
    }
}
