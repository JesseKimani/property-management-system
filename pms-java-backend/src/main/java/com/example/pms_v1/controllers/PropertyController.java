package com.example.pms_v1.controllers;

import com.example.pms_v1.dto.PropertyDto;
import com.example.pms_v1.models.Property;
import com.example.pms_v1.services.LandlordService;
import com.example.pms_v1.services.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final LandlordService landlordService;

    @PostMapping("/create-property")
    public ResponseEntity<Property> createProperty(@RequestBody PropertyDto.PropertyRequest request, @RequestParam Long landlordId) {
        Property property = propertyService.createProperty(request, landlordId);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/get-properties")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();

        return ResponseEntity.ok(properties);
    }

    @GetMapping("/get-properties-by-landlord/{landlordId}")
    public ResponseEntity<List<Property>> getPropertiesByLandlord(@PathVariable Long landlordId) {
        List<Property> properties = propertyService.getPropertiesByLandlordId(landlordId);
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/update-property/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody PropertyDto.PropertyRequest request) {
        Property updatedProperty = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/delete-property/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }
}
