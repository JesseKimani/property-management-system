package com.example.pms_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandlordResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String identity;
    private String pin;
}