package com.kargobaji.kargobaji.openAPI.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestAreaInfo {
    private String stdRestNm;

    private String latitude;
    private String longitude;

    private String telNo;
    private String routeName;

    private List<String> brands;

    private List<String> facilities;

    private String gasolinePrice;
    private String diselPrice;
    private String lpgPrice;

    private List<MenuInfo> menus;

    @Data
    @Builder
    public static class MenuInfo{
        private String foodNm;
        private String foodCost;
    }
}
