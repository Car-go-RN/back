package com.kargobaji.kargobaji.openAPI;

import lombok.Getter;

@Getter
public enum OpenApiType{
    BRAND("/restBrandList", 7),
    FOOD("/restBestfoodList", 68),
    FACILITIES("/restConvList", 13);

    private final String endpoint;
    private final int maxPage;

    OpenApiType(String endpoint, int maxPage) {
        this.endpoint = endpoint;
        this.maxPage = maxPage;
    }
}
