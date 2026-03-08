package com.example.pms_v1.controllers;

import com.example.pms_v1.dto.AuthDto;
import com.example.pms_v1.models.User;
import com.example.pms_v1.services.UserService;
import com.example.pms_v1.models.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor

public class AdminController {

    private final UserService userService;

    @PostMapping("/create-admin")
    public ResponseEntity<User> createAdmin(@RequestBody AuthDto.UserRequest request, @RequestParam Long companyId) {
        User admin = userService.createUser(request, RoleType.ADMIN.name(), companyId);
//        return ResponseEntity.ok(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }


    @PostMapping("/create-user")
    public ResponseEntity<AuthDto.UserInfo> createUser(@RequestBody AuthDto.UserRequest request, @RequestParam Long companyId) {

        User user = userService.createUser(request, RoleType.USER.name(), companyId);

        AuthDto.CompanyInfo companyInfo = null;
        if (user.getCompany() != null) {
            companyInfo = AuthDto.CompanyInfo.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .build();
        }

        Set<AuthDto.RoleInfo> roleInfos = user.getRoles().stream()
                .map(role -> AuthDto.RoleInfo.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        AuthDto.UserInfo userInfo = AuthDto.UserInfo.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .email(user.getEmail())
                .company(companyInfo)
                .roles(roleInfos)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
    }




    @GetMapping("/admins")
    public ResponseEntity<List<AuthDto.UserInfo>> getAllAdmins(@RequestParam(required = false) Long companyId) {
        List<User> admins = (companyId != null)
                ? userService.getUsersByRoleAndCompany(RoleType.ADMIN.name(), companyId)
                : userService.getUsersByRole(RoleType.ADMIN.name());

        List<AuthDto.UserInfo> userInfoList = admins.stream().map(user -> {
            AuthDto.CompanyInfo companyInfo = null;
            if (user.getCompany() != null) {
                companyInfo = AuthDto.CompanyInfo.builder()
                        .id(user.getCompany().getId())
                        .name(user.getCompany().getName())
                        .build();
            }

            Set<AuthDto.RoleInfo> roleInfos = user.getRoles().stream().map(role ->
                    AuthDto.RoleInfo.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .build()
            ).collect(Collectors.toSet());

            return AuthDto.UserInfo.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .email(user.getEmail())
                    .company(companyInfo)
                    .roles(roleInfos)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userInfoList);
    }


    @GetMapping("/users")
    public ResponseEntity<List<AuthDto.UserInfo>> getAllUsers(@RequestParam(required = false) Long companyId) {
        List<User> users = (companyId != null)
                ? userService.getUsersByRoleAndCompany(RoleType.USER.name(), companyId)
                : userService.getUsersByRole(RoleType.USER.name());

        List<AuthDto.UserInfo> userInfoList = users.stream().map(user -> {
            AuthDto.CompanyInfo companyInfo = null;
            if (user.getCompany() != null) {
                companyInfo = AuthDto.CompanyInfo.builder()
                        .id(user.getCompany().getId())
                        .name(user.getCompany().getName())
                        .build();
            }

            Set<AuthDto.RoleInfo> roleInfos = user.getRoles().stream().map(role ->
                    AuthDto.RoleInfo.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .build()
            ).collect(Collectors.toSet());

            return AuthDto.UserInfo.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .email(user.getEmail())
                    .company(companyInfo)
                    .roles(roleInfos)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userInfoList);
    }


    @PutMapping("/update-user/{id}")
    public ResponseEntity<AuthDto.UserInfo> updateUser(@PathVariable Long id, @RequestBody AuthDto.UserRequest request) {
        User updatedUser = userService.updateUser(id, request);

        AuthDto.CompanyInfo companyInfo = null;
        if (updatedUser.getCompany() != null) {
            companyInfo = AuthDto.CompanyInfo.builder()
                    .id(updatedUser.getCompany().getId())
                    .name(updatedUser.getCompany().getName())
                    .build();
        }

        Set<AuthDto.RoleInfo> roleInfos = updatedUser.getRoles().stream()
                .map(role -> AuthDto.RoleInfo.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        AuthDto.UserInfo userInfo = AuthDto.UserInfo.builder()
                .id(updatedUser.getId())
                .firstname(updatedUser.getFirstname())
                .email(updatedUser.getEmail())
                .company(companyInfo)
                .roles(roleInfos)
                .build();

        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
