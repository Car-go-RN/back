package com.kargobaji.kargobaji.openAPI;


import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenApiManager {
    private final RestAreaRepository restAreaRepository;

    private String BASE_URL = "https://data.ex.co.kr/openapi/restinfo";

    private String brand = "/restBrandList";
    private String food = "/restBestfoodList";
    private String facilities = "/restConvList";

    @Value("${open-api.key}")
    private String key;

    private String type = "&type=json";
    private String numOfRows = "&numOfRows=100";
    private String pageNo = "&pageNo=1";

    private String makeUrl() throws UnsupportedEncodingException {
        return BASE_URL
                + brand
                + key
                + type
                + numOfRows
                + pageNo;
    }

    // 공공데이터 api 호출
    public ResponseEntity<?> fetch() throws UnsupportedEncodingException {
        System.out.println(makeUrl());
        RestTemplate restTemplate = new RestTemplate(); // Http 요청에 대한 응답을 처리, json -> java 객체로 변환시키는 기능이 탑재되어 있음.
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders()); // 클라이언트 -> 서버로 보낼 요청 데이터를 담는 객체(header + body), 지금은 요청에 대한 header(부가 정보 ex) 인증)만 정보 담음.
        ResponseEntity<Map> resultMap = restTemplate.exchange(makeUrl(), HttpMethod.GET, entity, Map.class); // Json응답을 Map 형태로 파싱.
        System.out.println(resultMap.getBody());
        return resultMap;
    }

    // 외부 데이터 entity에 저장
    public void fetchAndSave() throws ParseException{
        try{
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(makeUrl(), String.class);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
            JSONArray dataList = (JSONArray) jsonObject.get("list");

            for(Object o : dataList){
                JSONObject item = (JSONObject) o;

                RestArea restArea = new RestArea();
                restArea.setStdRestNm((String) item.get("stdRestNm"));

                // brand
                restArea.setBrdName((String) item.get("brdName"));
                restArea.setStime((String) item.get("stime"));
                restArea.setEtime((String) item.get("etime"));

                // food
//                restArea.setFoodNm((String) item.get("foodNm"));
//                restArea.setFoodCost((String) item.get("foodCost"));
//
                // facility
//                restArea.setPsName((String) item.get("psName"));


                restAreaRepository.save(restArea);
            }
        }
        catch (Exception e){
            System.out.println("에러 발생 : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
