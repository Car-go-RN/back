package com.kargobaji.kargobaji.openAPI;


import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFood;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaGas;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaGasRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@RequiredArgsConstructor
@Data
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

    private String makeUrl(OpenApiType apiType, int pageNo) {
        return BASE_URL
                + apiType.getEndpoint()
                + key
                + type
                + numOfRows
                + "&pageNo=" + pageNo;
    }

    // 공공데이터 api 호출
    public ResponseEntity<Map> fetch(OpenApiType apiType, int pageNo) {
        String url = makeUrl(apiType, pageNo);
        System.out.println(url);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<Map> resultMap = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return resultMap;
    }

    // 외부 데이터 entity에 저장
    public void fetchAndSave(){
        syncMonthlyData();
        syncWeeklyData();
    }

    // 매월 15일 4시 마다 brand, facility, food 데이터 업데이트
    @Scheduled(cron = "0 0 4 15 * *")
    public void syncMonthlyData(){
        syncBrands();
        syncFacilities();
        syncFoods();
    }

    // 매주 월요일마다 gas 데이터 업데이트
    @Scheduled(cron = "0 0 4 * * Mon")
    public void syncWeeklyData(){
        syncGases();
    }

    // 브랜드
    private void syncBrands() {
        Set<String> existingKeys = new HashSet<>();

        for(int page=1; page<=OpenApiType.BRAND.getMaxPage(); page++){
            JSONArray list = fetchList(OpenApiType.BRAND, page);
            if(list == null) continue;

            for(Object obj : list){
                JSONObject item = (JSONObject) obj;
                String stdRestNm = (String) item.get("stdRestNm");
                String brdName = (String) item.get("brdName");

                if(stdRestNm == null || brdName == null) continue;

                existingKeys.add(stdRestNm + brdName);

                // 중복 데이터 확인
                RestAreaBrand existing = restAreaBrandRepository.findByStdRestNmAndBrdName(stdRestNm, brdName);
                if(existing == null){
                    restAreaBrandRepository.save(
                            RestAreaBrand.builder()
                                    .brdName(brdName)
                                    .stdRestNm(stdRestNm)
                                    .build()
                    );
                }
            }
        }
        
        List<RestAreaBrand> all = restAreaBrandRepository.findAll();
        for(RestAreaBrand b : all){
            if(!existingKeys.contains(b.getStdRestNm() + b.getBrdName())){
                restAreaBrandRepository.delete(b);
            }
        }
    }

    // 시설
    private void syncFacilities() {
        Set<String> existingKeys = new HashSet<>();

        for(int page=1;page<=OpenApiType.FACILITIES.getMaxPage(); page++){
            JSONArray list = fetchList(OpenApiType.FACILITIES, page);
            if(list == null) continue;

            for(Object obj : list){
                JSONObject item = (JSONObject) obj;
                String stdRestNm = (String) item.get("stdRestNm");
                String psName = (String) item.get("psName");

                if(stdRestNm == null || psName == null) continue;

                existingKeys.add(stdRestNm + psName);
                RestAreaFacility existing = restAreaFacilityRepository.findByStdRestNmAndPsName(stdRestNm, psName);
                if(existing == null){
                    restAreaFacilityRepository.save(
                            RestAreaFacility.builder()
                                    .psName(psName)
                                    .stdRestNm(stdRestNm)
                                    .build()
                    );
                }
            }
        }

        List<RestAreaFacility> all = restAreaFacilityRepository.findAll();
        for(RestAreaFacility f : all){
            if(!existingKeys.contains(f.getStdRestNm() + f.getPsName())){
                restAreaFacilityRepository.delete(f);
            }
        }
    }

    // 음식
    private void syncFoods(){
        Set<String> existingKeys = new HashSet<>();

        for(int page=1; page<=OpenApiType.FOOD.getMaxPage(); page++){
            JSONArray list = fetchList(OpenApiType.FOOD, page);
            if(list == null) continue;

            for(Object obj : list){
                JSONObject item = (JSONObject) obj;
                String stdRestNm = (String) item.get("stdRestNm");
                String foodNm = (String) item.get("foodNm");
                String foodCost = (String) item.get("foodCost");

                if(stdRestNm == null || foodNm == null || foodCost == null) continue;

                existingKeys.add(stdRestNm + foodNm);
                RestAreaFood existing = restAreaFoodRepository.findByStdRestNmAndFoodNm(stdRestNm, foodNm);
                if(existing == null){
                    restAreaFoodRepository.save(
                            RestAreaFood.builder()
                                    .foodCost(foodCost)
                                    .foodNm(foodNm)
                                    .stdRestNm(stdRestNm)
                                    .build()
                    );
                }
                else if(!existing.getFoodCost().equals(foodCost)){
                    existing.setFoodCost(foodCost);
                    restAreaFoodRepository.save(existing);
                }
            }
        }

        List<RestAreaFood> all = restAreaFoodRepository.findAll();
        for(RestAreaFood f : all){
            if(!existingKeys.contains(f.getStdRestNm() + f.getFoodNm())){
                restAreaFoodRepository.delete(f);
            }
        }
    }

    // 기름
    private void syncGases() {
        restAreaGasRepository.deleteAll();

        for(int page=1; page<=OpenApiType.GAS.getMaxPage(); page++){
            JSONArray list = fetchList(OpenApiType.GAS, page);
            if(list == null) continue;

            for(Object obj : list){
                JSONObject item = (JSONObject) obj;
                String stdRestNm = ((String) item.get("serviceAreaName")).replace("주유소", "휴게소");
                String gasoline = (String) item.get("gasolinePrice");
                String disel = (String) item.get("diselPrice");
                String lpg = (String) item.get("lpgPrice");

                restAreaGasRepository.save(
                        RestAreaGas.builder()
                                .gasolinePrice(gasoline)
                                .diselPrice(disel)
                                .lpgPrice(lpg)
                                .stdRestNm(stdRestNm)
                                .build()
                );
            }
        }
    }

    private JSONArray fetchList(OpenApiType apiType, int page){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(makeUrl(apiType, page), String.class);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
            return (JSONArray) jsonObject.get("list");
        }
        catch(Exception e){
            System.out.println("[" + apiType.name() + "] 페이지" + page + " 에러: " + e.getMessage());
            return null;
        }
    }
}
