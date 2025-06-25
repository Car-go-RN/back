package com.kargobaji.kargobaji.search.path;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoRouteManager {

    private final WebClient webClient;

    @Value("${kakao-rest-api}")
    private String kakaoApiKey;

    // 추천경로의 좌표 리스트 반환 (Kakao mobility API 사용)
    public List<double[]> getRoutePoints(double originX, double originY, double desX, double desY){
        String uri = "https://apis-navi.kakaomobility.com"
                + "/v1/directions?origin=" + originX + "," + originY + "&destination=" + desX + "," + desY;

        // API 호출 및 응답 처리
        Map<String, Object> response = webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        List<double[]> points = new ArrayList<>();

        if(response != null){
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");

            if(routes != null && !routes.isEmpty()){
                Map<String, Object> firstRoute = routes.getFirst();
                List<Map<String, Object>> sections = (List<Map<String, Object>>) firstRoute.get("sections");

                for(Map<String, Object> section : sections){
                    List<Map<String, Object>> roads = (List<Map<String, Object>>) section.get("roads");

                    for(Map<String, Object> road : roads){
                        List<Double> vertexes = (List<Double>) road.get("vertexes");

                        for(int i=0; i<vertexes.size(); i+=2){
                            double x = vertexes.get(i); // 경도
                            double y = vertexes.get(i+1); // 위도
                            points.add(new double[]{x, y});
                        }
                    }
                }
            }
        }
        return points;
    }
}
