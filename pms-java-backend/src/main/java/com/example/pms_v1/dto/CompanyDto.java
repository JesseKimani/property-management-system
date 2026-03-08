package com.example.pms_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CompanyDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyRequest {
        private String name;
        private String email;
        private String phone;
        private String postal_address;
        private String location;
    }
}

