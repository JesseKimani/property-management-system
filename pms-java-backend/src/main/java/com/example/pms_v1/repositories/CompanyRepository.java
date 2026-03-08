package com.example.pms_v1.repositories;

import com.example.pms_v1.models.Company;
import com.example.pms_v1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long id);

    Optional<Company> findByEmail(String email);
    boolean existsByEmail(String email);
}
