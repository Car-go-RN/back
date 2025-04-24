package com.kargobaji.kargobaji.openAPI;


import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFood;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaGas;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaGasRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenApiManager {
    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;
    private final RestAreaGasRepository restAreaGasRepository;


    private String BASE_URL = "https://data.ex.co.kr/openapi";

    @Value("${open-api.key}")
    private String key;

    private String type = "&type=json";
    private String numOfRows = "&numOfRows=100";

    private String makeUrl(OpenApiType apiType, int pageNo) throws UnsupportedEncodingException {
        return BASE_URL
                + apiType.getEndpoint()
                + key
                + type
                + numOfRows
                + "&pageNo=" + pageNo;
    }

    // 공공데이터 api 호출
    public ResponseEntity<Map> fetch(OpenApiType apiType, int pageNo) throws UnsupportedEncodingException{
        String url = makeUrl(apiType, pageNo);
        System.out.println(url);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Map> resultMap = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return resultMap;
    }

    // 외부 데이터 entity에 저장
    public void fetchAndSave() throws ParseException{
        for(OpenApiType apiType : OpenApiType.values()){
            fetchAndSaveByType(apiType);
        }
    }

    private void fetchAndSaveByType(OpenApiType apiType){
        RestTemplate restTemplate = new RestTemplate();
        JSONParser parser = new JSONParser();

        for(int page=1; page<=apiType.getMaxPage(); page++){
            try{
                String jsonString = restTemplate.getForObject(makeUrl(apiType, page), String.class);
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                JSONArray dataList = (JSONArray) jsonObject.get("list");

                for(Object o : dataList){
                    JSONObject item = (JSONObject) o;

                    RestAreaBrand restAreaBrand = new RestAreaBrand();
                    RestAreaFacility restAreaFacility = new RestAreaFacility();
                    RestAreaFood restAreaFood = new RestAreaFood();
                    RestAreaGas restAreaGas = new RestAreaGas();

                    switch (apiType){
                        case BRAND:
                            if(item.get("stdRestNm") == null || item.get("brdName")==null){
                                break;
                            }
                            restAreaBrand.setStdRestNm((String) item.get("stdRestNm"));
                            restAreaBrand.setBrdName((String) item.get("brdName"));
                            restAreaBrandRepository.save(restAreaBrand);
                            break;

                        case FOOD:
                            if(item.get("stdRestNm")==null || item.get("foodNm")==null || item.get("foodCost")==null){
                                break;
                            }
                            restAreaFood.setStdRestNm((String) item.get("stdRestNm"));
                            restAreaFood.setFoodNm((String) item.get("foodNm"));
                            restAreaFood.setFoodCost((String) item.get("foodCost"));
                            restAreaFoodRepository.save(restAreaFood);
                            break;

                        case FACILITIES:
                            if(item.get("stdRestNm")==null || item.get("psName")==null){
                                break;
                            }
                            restAreaFacility.setStdRestNm((String) item.get("stdRestNm"));
                            restAreaFacility.setPsName((String) item.get("psName"));
                            restAreaFacilityRepository.save(restAreaFacility);
                            break;

                        case GAS:
                            String srNm = item.get("serviceAreaName").toString();
                            srNm.replace("주유소", "휴게소");
                            restAreaGas.setStdRestNm(srNm);

                            restAreaGas.setGasolinePrice((String) item.get("gasolinePrice"));
                            restAreaGas.setDiselPrice((String) item.get("diselPrice"));
                            restAreaGas.setLpgPrice((String) item.get("lpgPrice"));

                            restAreaGasRepository.save(restAreaGas);
                    }
                }
            }
            catch (Exception e){
                System.out.println("[" + apiType.name() + "] 페이지" + page + "처리 중 에러" + e.getMessage());
            }
        }

    }

}
