package com.example.pms_v1.controllers;

import com.example.pms_v1.dto.AuthDto;
import com.example.pms_v1.dto.CompanyDto;
import com.example.pms_v1.models.User;
import com.example.pms_v1.models.Company;
import com.example.pms_v1.models.enums.RoleType;
import com.example.pms_v1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/developer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DEVELOPER')") // Ensures all endpoints are restricted to developers
public class DeveloperController {

    private final UserService userService;

    @PostMapping("/create-company")
    public ResponseEntity<Company> createCompany(@RequestBody CompanyDto.CompanyRequest request) {
        Company company = userService.createCompany(request);
        return  ResponseEntity.ok(company);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<User> createAdmin(@RequestBody AuthDto.UserRequest request, @RequestParam Long companyId) {
        User admin = userService.createUser(request, RoleType.ADMIN.name(), companyId);
//        return ResponseEntity.ok(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody AuthDto.UserRequest request, @RequestParam Long companyId) {
        User user = userService.createUser(request, RoleType.USER.name(), companyId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins(@RequestParam(required = false) Long companyId) {
        List<User> admins = (companyId != null)
                ? userService.getUsersByRoleAndCompany(RoleType.ADMIN.name(), companyId)
                : userService.getUsersByRole(RoleType.ADMIN.name());
        return ResponseEntity.ok(admins);
    }

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

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
