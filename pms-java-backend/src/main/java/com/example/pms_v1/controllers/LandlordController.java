package com.example.pms_v1.controllers;

import com.example.pms_v1.dto.LandlordDto;
import com.example.pms_v1.dto.LandlordResponse;
import com.example.pms_v1.models.Landlord;
import com.example.pms_v1.services.LandlordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlords")
@RequiredArgsConstructor
public class LandlordController {

    private final LandlordService landlordService;

    @PostMapping("/create-landlord")
    public ResponseEntity<Landlord> createLandlord(@RequestBody LandlordDto.LandlordRequest request) {
        Landlord landlord = landlordService.createLandlord(request);
        return  ResponseEntity.ok(landlord);
    }

    @GetMapping("/get-landlords")
    public ResponseEntity<List<LandlordResponse>> getAllLandlords() {
        List<LandlordResponse> landlords = landlordService.getAllLandlords();

        return ResponseEntity.ok(landlords);
    }


    @PutMapping("/update-landlord/{id}")
    public ResponseEntity<Landlord> updateLandlord(@PathVariable Long id, @RequestBody LandlordDto.LandlordRequest request) {
        Landlord updatedLandlord = landlordService.updateLandlord(id, request);
        return ResponseEntity.ok(updatedLandlord);
    }

    @DeleteMapping("/delete-landlord/{id}")
    public ResponseEntity<String> deleteLandlord(@PathVariable Long id) {
        landlordService.deleteLandlord(id);
        return ResponseEntity.ok("Landlord deleted successfully");
    }
}
