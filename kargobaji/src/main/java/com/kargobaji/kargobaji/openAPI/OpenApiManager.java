package com.kargobaji.kargobaji.openAPI;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class OpenApiManager {
    private String BASE_URL = "https://data.ex.co.kr/openapi/restinfo";

    private String brand = "/restBrandList";
    private String food = "/restBestfoodList";
    private String facilities = "/restConvList";

    @Value("${open-api.key}")
    private String key;

    private String type = "&type=json";
    private String numOfRows = "&numOfRows=220";

    private String makeUrl() throws UnsupportedEncodingException {
        return BASE_URL
                + facilities
                + key
                + type
                + numOfRows;
    }

    // 공공데이터 api 호출
    public ResponseEntity<?> fetch() throws UnsupportedEncodingException {
        System.out.println(makeUrl());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Map> resultMap = restTemplate.exchange(makeUrl(), HttpMethod.GET, entity, Map.class);
        System.out.println(resultMap.getBody());
        return resultMap;
    }






}
