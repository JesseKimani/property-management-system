package com.example.pms_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PropertyDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PropertyRequest {
        private String name;
        private String location;
        private Long landlord;
    }
}

