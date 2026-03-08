package com.example.pms_v1.config;

import com.example.pms_v1.models.Permission;
import com.example.pms_v1.models.Role;
import com.example.pms_v1.models.User;
import com.example.pms_v1.models.enums.PermissionType;
import com.example.pms_v1.models.enums.RoleType;
import com.example.pms_v1.repositories.PermissionRepository;
import com.example.pms_v1.repositories.RoleRepository;
import com.example.pms_v1.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor

public class DatabaseSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedPermissions();

        seedRoles();

        seedDeveloper();
    }

    private void seedPermissions() {
        if (permissionRepository.count() == 0) {
            for (PermissionType permType : PermissionType.values()) {
                Permission permission = Permission.builder()
                        .name(permType.name())
                        .description("Permission to " + permType.name().toLowerCase().replace('_', ' '))
                        .build();

                permissionRepository.save(permission);
            }
        }
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            // Developer Role
            Role developerRole = createRole(
                    RoleType.DEVELOPER.name(),
                    "Developer with all permissions",
                    getAllPermissions()
            );

            Set<Permission> adminPermissions = new HashSet<>();
            for (PermissionType permType : PermissionType.values()) {
                if (!permType.name().contains("DEVELOPER")) {
                    permissionRepository.findByName(permType.name())
                            .ifPresent(adminPermissions::add);
                }
            }

            // Admin Role
            Role adminRole = createRole(
                    RoleType.ADMIN.name(),
                    "Admin with permissions to manage users and other admins",
                    adminPermissions
            );

            // User Role
            Set<Permission> userPermissions = new HashSet<>();
            permissionRepository.findByName(PermissionType.READ_USER.name())
                    .ifPresent(userPermissions::add);
            Role userRole = createRole(
                    RoleType.USER.name(),
                    "Regular user with limited permissions",
                    userPermissions
            );

            roleRepository.saveAll(Arrays.asList(developerRole, adminRole, userRole));
        }
    }

    private Role createRole(String name, String description, Set<Permission> permissions) {
        return Role.builder()
                .name(name)
                .description(description)
                .permissions(permissions)
                .build();
    }

    private Set<Permission> getAllPermissions() {
        Set<Permission> allPermissions = new HashSet<>();
        permissionRepository.findAll().forEach(allPermissions::add);
        return allPermissions;
    }

    private void seedDeveloper() {
        if (userRepository.count() == 0) {
            roleRepository.findByName(RoleType.DEVELOPER.name())
                    .ifPresent(developerRole -> {
                        User developer = User.builder()
                                .firstname("Jesse")
                                .lastname("Irungu")
                                .email("jesseirungu3@gmail.com")
                                .phone("0743784792")
                                .password(passwordEncoder.encode("Kimani01"))
                                .roles(Set.of(developerRole))
                                .build();

                        userRepository.save(developer);
                    });
        }
    }
}
