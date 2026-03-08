package com.example.pms_v1.repositories;

import com.example.pms_v1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByCompanyId(Long companyId);

    List<User> findByRoles_Name(String roleName);
    List<User> findByRoles_NameAndCompany_Id(String roleName, Long companyId);

}