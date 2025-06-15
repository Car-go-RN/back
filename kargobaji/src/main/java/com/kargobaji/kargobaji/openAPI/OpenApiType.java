package com.kargobaji.kargobaji.openAPI;

import lombok.Getter;

@Getter
public enum OpenApiType{
    BRAND("/restinfo/restBrandList", 7),
    FOOD("/restinfo/restBestfoodList", 68),
    FACILITIES("/restinfo/restConvList", 13),
    GAS("/business/curStateStation", 3);

    private final String endpoint;
    private final int maxPage;

    OpenApiType(String endpoint, int maxPage) {
        this.endpoint = endpoint;
        this.maxPage = maxPage;
    }
}
