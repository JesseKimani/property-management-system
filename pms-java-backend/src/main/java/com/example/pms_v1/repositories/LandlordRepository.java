package com.example.pms_v1.repositories;

import com.example.pms_v1.models.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Long> {
    Optional<Landlord> findById(Long id);

    Optional<Landlord> findByEmail(String email);
    boolean existsByEmail(String email);
}
