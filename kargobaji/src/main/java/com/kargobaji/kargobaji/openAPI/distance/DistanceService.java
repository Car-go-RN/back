package com.kargobaji.kargobaji.openAPI.distance;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class DistanceService {

    private final RestAreaRepository restAreaRepository;
    private final WebClient webClient;

    @Value("${kakao-rest-api}")
    private String restApi;

    public Map<String, Object> calculateDistance(double currentLat, double currentLng, String stdRestNm) {
        RestArea restArea = restAreaRepository
                .findByStdRestNm(stdRestNm)
                .orElseThrow(() -> new RuntimeException("해당 휴게소를 찾을 수 없습니다."));

        String url = String.format(
                "https://apis-navi.kakaomobility.com/v1/directions?origin=%f,%f&destination=%f,%f&priority=RECOMMEND&summary=true",
                currentLng, currentLat,
                restArea.getLongitude(), restArea.getLatitude()
        );

//        System.out.println("restAreaNm : " + stdRestNm);
//        System.out.println("culn : " + currentLng + " cula : " + currentLat);
//        System.out.println("getln : " + restArea.getLongitude() + "getla : " + restArea.getLatitude());

        String responseBody = webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApi)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(error -> Mono.error(new RuntimeException("Kakao Api 호출 실패 : " + error.getMessage())))
                .block();

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responseBody);
            JSONArray routes = (JSONArray) json.get("routes");

            if (routes.isEmpty()) {
                throw new RuntimeException("Kakao 응답에 route정보가 없습니다.");
            }

            JSONObject firstRoute = (JSONObject) routes.getFirst();
            JSONArray sections = (JSONArray) firstRoute.get("sections");
            JSONObject section = (JSONObject) sections.getFirst();

            long distanceMeters = (Long) section.get("distance");

            Map<String, Object> result = new HashMap<>();
            result.put("restAreaNm", restArea.getRestAreaNm());
            result.put("distanceKm", (int) (distanceMeters / 1000.0));
            result.put("message", "거리계산 성공(sections 기반)");

            return result;
        } catch (Exception e) {
//            System.out.println("stdRestNm : " + stdRestNm);
//            System.out.println("culn : " + currentLng + " cula : " + currentLat);
//            System.out.println("getln : " + restArea.getLongitude() + "getla : " + restArea.getLatitude());

            throw new RuntimeException("Kakao 응답 파싱 오류: " + e.getMessage());
        }
    }
}