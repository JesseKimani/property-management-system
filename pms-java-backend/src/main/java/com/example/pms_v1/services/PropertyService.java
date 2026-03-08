package com.example.pms_v1.services;

import com.example.pms_v1.dto.LandlordDto;
import com.example.pms_v1.dto.PropertyDto;
import com.example.pms_v1.models.Company;
import com.example.pms_v1.models.Landlord;
import com.example.pms_v1.models.Property;
import com.example.pms_v1.repositories.LandlordRepository;
import com.example.pms_v1.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Observable;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final LandlordRepository landlordRepository;
    private final LandlordService landlordService;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> getPropertiesByLandlordId(Long landlordId) {
        Landlord landlord = landlordService.getLandlordById(landlordId);
        return propertyRepository.findByLandlordId(landlordId);
    }

    public Property createProperty(PropertyDto.PropertyRequest request, Long landlordId) {
        Landlord landlord = landlordRepository.findById(landlordId)
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        Property property = Property.builder()
                .name(request.getName())
                .location(request.getLocation())
                .landlord(landlord)
                .build();

        return propertyRepository.save(property);
    }

    public Property updateProperty(Long id, PropertyDto.PropertyRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setName(request.getName());
        property.setLocation(request.getLocation());

        return propertyRepository.save(property);
    }

    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        propertyRepository.deleteById(id);
    }

}
