package com.kargobaji.kargobaji.openAPI.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// RestAreaDetailDto.java
// RestAreaDetailDto.java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestAreaDetailDto {
    private Long id;
    private String stdRestNm;
    private String gasolinePrice;
    private String diselPrice;
    private String lpgPrice;
    private String roadAddress;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String restAreaNm;

    private List<String> brandNames;
    private List<String> facilityNames;
    private List<FoodDto> foods;

    @Data
    @Builder
    public static class FoodDto {
        private String foodNm;
        private String foodCost;
    }
}


