package com.example.pms_v1.services;

import com.example.pms_v1.dto.LandlordDto;
import com.example.pms_v1.dto.LandlordResponse;
import com.example.pms_v1.models.Landlord;
import com.example.pms_v1.models.Property;
import com.example.pms_v1.repositories.LandlordRepository;
import com.example.pms_v1.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LandlordService {

    private final LandlordRepository landlordRepository;

//    public List<Landlord> getAllLandlords() {
//        return landlordRepository.findAll();
//    }

    public List<LandlordResponse> getAllLandlords() {
        return landlordRepository.findAll().stream()
                .map(landlord -> LandlordResponse.builder()
                        .id(landlord.getId())
                        .name(landlord.getName())
                        .email(landlord.getEmail())
                        .phone(landlord.getPhone())
                        .identity(landlord.getIdentity())
                        .pin(landlord.getPin())
                        .build())
                .collect(Collectors.toList());
    }

    public Landlord getLandlordById(Long id) {
        return landlordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landlord not found"));
    }

    public Landlord getLandlordByEmail(String email) {
        return landlordRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Landlord not found"));
    }

    public Landlord createLandlord(LandlordDto.LandlordRequest request) {
        if (landlordRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Landlord with this email already exists");
        }

        Landlord landlord = Landlord.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .identity(request.getIdentity())
                .pin(request.getPin())
                .build();

        return landlordRepository.save(landlord);
    }

    public Landlord updateLandlord(Long id, LandlordDto.LandlordRequest request) {
        Landlord landlord = landlordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        landlord.setName(request.getName());

        if (!landlord.getEmail().equals(request.getEmail()) && landlordRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        landlord.setEmail(request.getEmail());

        landlord.setPhone(request.getPhone());
        landlord.setIdentity(request.getIdentity());
        landlord.setPin(request.getPin());


        return landlordRepository.save(landlord);
    }

    public void deleteLandlord(Long id) {
        Landlord landlord = landlordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        landlordRepository.deleteById(id);
    }

}