package com.kargobaji.kargobaji.local.Address.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPlaceResponse {
    private List<Document> documents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document{
        private String place_name;
        private String road_address_name;
        private String category_name;
        private String x; // 경도
        private String y; // 위도
        private String phone;
    }
}
