package com.example.pms_v1.services;

import com.example.pms_v1.dto.AuthDto;
import com.example.pms_v1.models.Role;
import com.example.pms_v1.models.User;
import com.example.pms_v1.models.Company;
import com.example.pms_v1.repositories.RoleRepository;
import com.example.pms_v1.repositories.UserRepository;
import com.example.pms_v1.repositories.CompanyRepository;
import com.example.pms_v1.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDto.AuthResponse authenticate(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Developer company will be null
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

        return AuthDto.AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .user(userInfo)
                .build();

    }

    public User createUser(AuthDto.UserRequest request, String roleName) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Company company = null;
        if (request.getCompany() != null) {
            company = companyRepository.findById(request.getCompany())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
//                .company(request.getCompany() != null ? request.getCompany().getId() : null)
                .company(company)
                .roles(Set.of(role))
                .build();

        return userRepository.save(user);
    }
}

