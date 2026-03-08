package com.example.pms_v1.repositories;

import com.example.pms_v1.models.Landlord;
import com.example.pms_v1.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findById(Long id);

    List<Property> findByLandlordId(Long landlordId);

}