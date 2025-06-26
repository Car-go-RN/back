package com.kargobaji.kargobaji.openAPI;


import com.kargobaji.kargobaji.openAPI.distance.DistanceService;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.entity.*;
import com.kargobaji.kargobaji.openAPI.repository.*;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Data
public class OpenApiManager {
    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;
    private final RestAreaRepository restAreaRepository;

    private final DistanceService distanceService;

    private String BASE_URL = "https://data.ex.co.kr/openapi";

    @Value("${open-api.key}")
    private String key;

    private String type = "&type=json";
    private String numOfRows = "&numOfRows=100";

    private String makeUrl(OpenApiType apiType, int pageNo) { // api 요청에 사용할 URL
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

        RestTemplate restTemplate = new RestTemplate(); // RestTemplate 정의
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders()); // 요청 header 정의

        // Url에 Get으로 header와 함께 요청을 보내서 응답 데이터를 Map으로 받음.
        // exchange는 HTTP 요청을 한 후 서버의 응답을 받아오는 역할 임.
        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    }

    // 외부 데이터 DB에 저장
    // brand, facility, food는 업서트 방식
    // gas는 전체 삭제 후 데이터 재대입
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

        for(int page=1; page<=OpenApiType.BRAND.getMaxPage(); page++){ // BRAND의 마지막 페이지까지 반복
            JSONArray list = fetchList(OpenApiType.BRAND, page); // BRAND 데이터를 가져옴.
            // null 확인
            if(list == null) continue;

            //
            for(Object obj : list){
                JSONObject item = (JSONObject) obj; // obj(Object 타입)을 JSONObject로 캐스팅(데이터 형 변환)

                //String으로 캐스팅하는 이유? : 자바는 강타입 언어, 즉 명확한 변수의 타입을 정의해야 됨. 하지만 Object는 명확하지 않기 때문에 String으로 캐스팅을 함.
                String stdRestNm = (String) item.get("stdRestNm"); // obj에서 "stdRestNm" 값을 string 캐스팅
                String brdName = (String) item.get("brdName"); // obj에서 "brdName" 값을 string 캐스팅

                // null 확인
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
        for(RestAreaBrand b : all) {
            // api 응답 데이터가 없을 시 해당 DB 데이터 행을 삭제 함.
            if (!existingKeys.contains(b.getStdRestNm() + b.getBrdName())) {
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

    // 주유소
    private void syncGases() {
        // 전체 삭제
        restAreaRepository.deleteAll();

        for(int page=1; page<=OpenApiType.GAS.getMaxPage(); page++){
            JSONArray list = fetchList(OpenApiType.GAS, page);
            if(list == null) continue;

            for(Object obj : list){
                JSONObject item = (JSONObject) obj;
                String stdRestNm = ((String) item.get("serviceAreaName")).replace("주유소", "휴게소");
                String gasoline = (String) item.get("gasolinePrice");
                String disel = (String) item.get("diselPrice");
                String lpg = (String) item.get("lpgPrice");

                restAreaRepository.save(
                        RestArea.builder()
                                .gasolinePrice(gasoline)
                                .diselPrice(disel)
                                .lpgPrice(lpg)
                                .stdRestNm(stdRestNm)
                                .build()
                );
            }
        }
    }

    // 데이터 조회
    private JSONArray fetchList(OpenApiType apiType, int page){
        try{
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(makeUrl(apiType, page), String.class); //apiType과 page로 만든 URL에 GET 요청을 보내고 String으로 응답을 받음.
            // 응답 데이터(JSON 문자열)를 JSON 객체로 파싱
            // parse : 문자열을 객체로 변환하는 메서드
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
            // list 라는 키에 해당하는 JSONArray를 꺼내고 리턴
            // 왜 JSONArray이냐? -> list가 배열 형태임. list[데이터값들]
            return (JSONArray) jsonObject.get("list");
        }
        catch(Exception e){
            System.out.println("[" + apiType.name() + "] 페이지" + page + " 에러: " + e.getMessage());
            return null;
        }
    }

    // 데이터 가져오기
    public List<Map<String, Object>> getData(
            // 파라미터 값 저장
            String table,
            List<String> fields,
            Map<String, String> filters,
            Integer limit
    ){

        List<?> entities = findEntitiesByTable(table);

        if(filters != null){
            filters.remove("field");
            filters.remove("limit");
        }

        entities = applyFilters(entities, filters);

        if(limit!=null && limit>0 && limit<entities.size()){
            entities = entities.subList(0, limit);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for(Object entity : entities){
            result.add(extractFields(entity, fields));
        }

        return result;
    }

    // 해당 되는 테이블 엔티티를 조회
    private List<?> findEntitiesByTable(String table) {
        return switch (table.toLowerCase()) {
            case "brand" -> restAreaBrandRepository.findAll();
            case "facility" -> restAreaFacilityRepository.findAll();
            case "food" -> restAreaFoodRepository.findAll();
            case "gas" -> restAreaRepository.findAll();
            default -> throw new IllegalArgumentException("테이블이 존재하지 않습니다. " + table);
        };
    }

    // 필터링 적용
    private List<?> applyFilters(List<?> entities, Map<String, String> filters) {
        // 필터링할 내용이 있는지 확인
        if (filters == null || filters.isEmpty()) return entities;

        // stream : 컬렉션(리스트, 배열 등)에서 데이터의 흐름(데이터의 상호작용 과정)을 처리
        return entities.stream()
                .filter(entity -> {
                    for (String key : filters.keySet()) {
                        String value = filters.get(key);
                        try {
                            Field field = entity.getClass().getDeclaredField(key);
                            field.setAccessible(true); // 해당 메소드가 private이기 때문에 field에게 접근 권한을 줌.
                            Object fieldValue = field.get(entity); // 해당 필드 값을 동적으로 가져옴.
                            if (fieldValue == null || !fieldValue.toString().contains(value)) {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return true;
                })
                // collect: 결과를 컬렉션으로 변한 (리스트로 변환)
                .collect(Collectors.toList());
    }

    // 조회
    private Map<String, Object> extractFields(Object entity, List<String> fields){ // entity : 필드 값을 추출할 객체, fields : 파라미터에서 받아온 필드 이름
        Map<String, Object> map = new HashMap<>(); // 필드 값을 저장 (필드이름 : 필드값)

        // 전체 추출
        if(fields == null || fields.isEmpty()){
            for(Field field : entity.getClass().getDeclaredFields()){ // 지정된 필드(null 이 아닌 필드)만큼 반복 및 필드 값을 가져옴.
                try{
                    field.setAccessible(true); // 필드에게 접근 권한을 줌.
                    map.put(field.getName(), field.get(entity)); // map에 필드 이름과 값을 저장
                }
                catch (IllegalAccessException e){
                    // 무시
                }
            }
        }
        else { // 특정 필드만 추출
            for(String fieldName : fields){
                try{
                    Field field = entity.getClass().getDeclaredField(fieldName); // 특정 필드 이름의 값을 가져옴.
                    field.setAccessible(true); // 필드에게 접근 권한을 줌.
                    map.put(fieldName, field.get(entity)); // 가져온 데이터를 map에 넣음.
                }
                catch (Exception e){
                    // 없는 필드 무시
                }
            }
        }
        return map;
    }

    // 휴게소 상세 정보 가져오기
    public List<RestAreaDetailDto> getRestAreaDetail(String stdRestNm, int page, Double currentLat, Double currentLng) {
        List<RestArea> restAreas;

        // 15개씩 출력 (이름 없을 시)
        if (stdRestNm == null || stdRestNm.trim().isEmpty()) {
            int pageSize = 15;
            int offset = (Math.max(page, 1) - 1) * pageSize;

            restAreas = restAreaRepository.findAll().stream()
                    .skip(offset)
                    .limit(pageSize)
                    .toList();
        } else {
            RestArea restArea = restAreaRepository.findByStdRestNm(stdRestNm)
                    .orElseThrow(() -> new IllegalArgumentException("휴게소를 찾을 수 없습니다."));
            restAreas = List.of(restArea);
        }

        return restAreas.stream().map(restArea -> {
            List<String> brands = restAreaBrandRepository.findByStdRestNm(restArea.getStdRestNm())
                    .stream().map(RestAreaBrand::getBrdName).toList();

            List<String> facilities = restAreaFacilityRepository.findByStdRestNm(restArea.getStdRestNm())
                    .stream().map(RestAreaFacility::getPsName).toList();

            List<RestAreaDetailDto.FoodDto> foods = restAreaFoodRepository.findByStdRestNm(restArea.getStdRestNm())
                    .stream().map(f -> new RestAreaDetailDto.FoodDto(f.getFoodNm(), f.getFoodCost()))
                    .toList();

            String distanceStr = null;

            if(stdRestNm != null & currentLat != null && currentLng != null){
                try{
                    Map<String, Object> distInfo = distanceService.calculateDistance(currentLat, currentLng, restArea.getStdRestNm());
                    Integer distanceKm = (Integer) distInfo.get("distanceKm");
                    double rawKm = distanceKm.doubleValue();

                    // 1Km 미만은 소수점 1자리, 이상은 정수로 표시
                    if(rawKm < 1.0){
                        distanceStr = String.format("%.1fkm", rawKm);
                    }
                    else {
                        distanceStr = String.format("%.0fkm", rawKm);
                    }
                }
                catch (Exception e){
                    throw new RuntimeException("거리 계산 실패: " + e.getMessage());
                }
            }

            return RestAreaDetailDto.builder()
                    .id(restArea.getId())
                    .stdRestNm(restArea.getStdRestNm())
                    .gasolinePrice(restArea.getGasolinePrice())
                    .diselPrice(restArea.getDiselPrice())
                    .lpgPrice(restArea.getLpgPrice())
                    .electric(restArea.getElectric())
                    .hydrogen(restArea.getHydrogen())
                    .roadAddress(restArea.getRoadAddress())
                    .phone(restArea.getPhone())
                    .latitude(restArea.getLatitude())
                    .longitude(restArea.getLongitude())
                    .restAreaNm(restArea.getRestAreaNm())
                    .brands(brands)
                    .facilities(facilities)
                    .foods(foods)
                    .distance(distanceStr)
                    .build();
        }).toList();
    }
}


