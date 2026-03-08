package com.example.pms_v1.services;

import com.example.pms_v1.dto.AuthDto;
import com.example.pms_v1.dto.CompanyDto;
import com.example.pms_v1.models.Role;
import com.example.pms_v1.models.User;
import com.example.pms_v1.models.Company;
import com.example.pms_v1.models.enums.RoleType;
import com.example.pms_v1.repositories.RoleRepository;
import com.example.pms_v1.repositories.UserRepository;
import com.example.pms_v1.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Fixed: Now properly injected

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<User> getAllUsers(User loggedInUser) {
        if (isDeveloper(loggedInUser)) {
            return userRepository.findAll();
        }
        return userRepository.findByCompanyId(loggedInUser.getCompany().getId());
    }

    public User getUserById(Long id, User loggedInUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!isDeveloper(loggedInUser) && !user.getCompany().getId().equals(loggedInUser.getCompany().getId())) {
            throw new RuntimeException("Access denied");
        }

        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Company createCompany(CompanyDto.CompanyRequest request) {
        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Company with this email already exists");
        }

        Company company = Company.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .postal_address(request.getPostal_address())
                .location(request.getLocation())
                .build();

        return companyRepository.save(company);
    }

    public User createUser(AuthDto.UserRequest request, String roleName, Long companyId) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        boolean isDeveloper = role.getName().equalsIgnoreCase("DEVELOPER");

        Company company = null;
        if (!isDeveloper) {
            if (companyId == null) {
                throw new RuntimeException("Company is required for ADMIN and USER roles");
            }
            company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
                .company(company)
                .build();

        // Save user first
        User savedUser = userRepository.save(user);

        // Send welcome email asynchronously
//        sendWelcomeEmailAsync(savedUser);

        return savedUser;
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoles_Name(roleName);
    }

    public List<User> getUsersByRoleAndCompany(String roleName, Long companyId) {
        return userRepository.findByRoles_NameAndCompany_Id(roleName, companyId);
    }

    public User updateUser(Long id, AuthDto.UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setPhone(request.getPhone());

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.deleteById(id);
    }

    private boolean isDeveloper(User user) {
        return user.getRoles().stream().anyMatch(role -> "DEVELOPER".equalsIgnoreCase(role.getName()));
    }

    @Async
    public void sendWelcomeEmailAsync(User user) {
        try {
            log.info("Attempting to send welcome email to: {}", user.getEmail());

            emailService.sendWelcomeEmail(
                    user.getEmail(),
                    user.getFirstname() + " " + user.getLastname()
            );

            log.info("Welcome email sent successfully to: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}. Error: {}", user.getEmail(), e.getMessage(), e);
            // Don't rethrow - we don't want to fail user creation because of email issues
        }
    }


}
















//package com.example.pms_v1.services;
//
//import com.example.pms_v1.dto.AuthDto;
//import com.example.pms_v1.dto.CompanyDto;
//import com.example.pms_v1.models.Role;
//import com.example.pms_v1.models.User;
//import com.example.pms_v1.models.Company;
//import com.example.pms_v1.models.enums.RoleType;
//import com.example.pms_v1.repositories.RoleRepository;
//import com.example.pms_v1.repositories.UserRepository;
//import com.example.pms_v1.repositories.CompanyRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import com.example.pms_v1.services.EmailService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final CompanyRepository companyRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    private EmailService emailService;
//
//    public List<Company> getAllCompanies() {
//        return companyRepository.findAll();
//    }
//
//    public List<User> getAllUsers(User loggedInUser) {
//        if (isDeveloper(loggedInUser)) {
//            return userRepository.findAll(); // Developer can see all users
//        }
//        return userRepository.findByCompanyId(loggedInUser.getCompany().getId()); // Restrict to company users
//    }
//
//    public User getUserById(Long id, User loggedInUser) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!isDeveloper(loggedInUser) && !user.getCompany().getId().equals(loggedInUser.getCompany().getId())) {
//            throw new RuntimeException("Access denied");
//        }
//
//        return user;
//    }
//
//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
//
//    public Company createCompany(CompanyDto.CompanyRequest request) {
//        if (companyRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Company with this email already exists");
//        }
//
//        Company company = Company.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .postal_address(request.getPostal_address())
//                .location(request.getLocation())
//                .build();
//
//        return companyRepository.save(company);
//    }
//
//    public User createUser(AuthDto.UserRequest request, String roleName, Long companyId) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        Role role = roleRepository.findByName(roleName)
//                .orElseThrow(() -> new RuntimeException("Role not found"));
//
//        boolean isDeveloper = role.getName().equalsIgnoreCase("DEVELOPER");
//
//        Company company = null;
//        if (!isDeveloper) {
//            if (companyId == null) {
//                throw new RuntimeException("Company is required for ADMIN and USER roles");
//            }
//            company = companyRepository.findById(companyId)
//                    .orElseThrow(() -> new RuntimeException("Company not found"));
//        }
//
//        User user = User.builder()
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .phone(request.getPhone())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .roles(Set.of(role))
//                .company(company) // Null for developers
//                .build();
//
//
//        sendWelcomeEmailAsync(user);
//
//        return userRepository.save(user);
//    }
//
//    public List<User> getUsersByRole(String roleName) {
//        return userRepository.findByRoles_Name(roleName);
//    }
//
//    public List<User> getUsersByRoleAndCompany(String roleName, Long companyId) {
//        return userRepository.findByRoles_NameAndCompany_Id(roleName, companyId);
//    }
//
//    public User updateUser(Long id, AuthDto.UserRequest request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        user.setFirstname(request.getFirstname());
//        user.setLastname(request.getLastname());
//        user.setPhone(request.getPhone());
//
//        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        user.setEmail(request.getEmail());
//
//        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        }
//
//        return userRepository.save(user);
//    }
//
//    public void deleteUser(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        userRepository.deleteById(id);
//    }
//
//    private boolean isDeveloper(User user) {
//        return user.getRoles().stream().anyMatch(role -> "DEVELOPER".equalsIgnoreCase(role.getName()));
//    }
//
//
//
//
//    @Async
//    public void sendWelcomeEmailAsync(User user) {
//        try {
//            emailService.sendWelcomeEmail(
//                    user.getEmail(),
//                    user.getFirstname() + " " + user.getLastname()
//            );
//
//            // Log successful email send
//            System.out.printf("Welcome email sent to: %s", user.getEmail());
//
//        } catch (Exception e) {
//            // Log error but don't fail user creation
//            System.err.println("Failed to send welcome email to: " + user.getEmail());
//            e.printStackTrace();
//        }
//    }
//
////    public void initiatePasswordReset(String email) {
////        User user = userRepository.findByEmail(email)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        // Generate reset token
////        String resetToken = UUID.randomUUID().toString();
////        user.setResetToken(resetToken);
////        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
////
////        userRepository.save(user);
////
////        // Send password reset email
////        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
////    }
//
//}
