package com.kargobaji.kargobaji.openAPI.dto;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFood;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestAreaDetailDto {
    private Long id;
    private String stdRestNm;
    private Double reviewAVG;

    private String gasolinePrice;
    private String diselPrice;
    private String lpgPrice;
    private String electric; // 전기 충전소 여부
    private String hydrogen; // 수소 충전소 여부
    private String roadAddress;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String restAreaNm;

    private String distance;

    private List<String> brands;
    private List<String> facilities;
    private List<FoodDto> foods;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FoodDto{
        private String foodNm;
        private String foodCost;
    }
}
