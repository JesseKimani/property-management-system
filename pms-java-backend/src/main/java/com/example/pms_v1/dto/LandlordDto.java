package com.example.pms_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LandlordDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LandlordRequest {
        private String name;
        private String email;
        private String phone;
        private String identity;
        private String pin;
    }
}

