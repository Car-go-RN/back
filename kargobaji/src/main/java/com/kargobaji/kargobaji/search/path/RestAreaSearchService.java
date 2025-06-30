package com.kargobaji.kargobaji.search.path;

import com.kargobaji.kargobaji.openAPI.config.InvalidRestAreaLocationException;
import com.kargobaji.kargobaji.openAPI.distance.DistanceService;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestAreaSearchService {
    private final RestAreaRepository restAreaRepository;
    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;
    private final DistanceService distanceService;

    public List<RestAreaDetailDto> findNearbyRestAreasSortedByDistance(List<double[]> routePoints){
        double radiusInKm = 0.2;

        List<RestArea> allRestAreas = restAreaRepository.findAll();
        Map<RestArea, Double> restAreaToMinDistance = new HashMap<>();

        for(double[] point : routePoints){
            double lon = point[0];
            double lat = point[1];

            for(RestArea restArea : allRestAreas){
                if(restArea.getLatitude() == null || restArea.getLongitude() == null){
                    throw new InvalidRestAreaLocationException(restArea.getStdRestNm());
                }

                double distanceHaversine = calculateDistance(lat, lon, restArea.getLatitude(), restArea.getLongitude());

                if(distanceHaversine <= radiusInKm){
                    try {
                        Map<String, Object> result = distanceService.calculateDistance(lat, lon, restArea.getStdRestNm());

                        Object rawDistance = result.get("distanceKm");
                        double accurateDistance = (rawDistance instanceof Integer intVal)
                                ? intVal
                                : (rawDistance instanceof Double doubleVal ? doubleVal : -1.0);

                        if (accurateDistance >= 0.0) {
                            restAreaToMinDistance.merge(restArea, accurateDistance, Math::min);
                        }
                    } catch (Exception e) {
                        // Kakao API 실패 시 무시
                    }
                }
            }
        }

        return restAreaToMinDistance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> toDetailDto(entry.getKey(), formatDistance(entry.getValue())))
                .toList();
    }

    // Haversine 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    // 포맷: 1km 이상이면 정수, 미만이면 소수점 첫째자리
    private String formatDistance(double distance) {
        return distance < 1.0
                ? String.format("%.1fkm", distance)
                : String.format("%.0fkm", distance);
    }

    // Dto 변환
    private RestAreaDetailDto toDetailDto(RestArea restArea, String distance) {
        List<String> brands = restAreaBrandRepository.findByStdRestNm(restArea.getStdRestNm())
                .stream().map(RestAreaBrand::getBrdName).toList();

        List<String> facilities = restAreaFacilityRepository.findByStdRestNm(restArea.getStdRestNm())
                .stream().map(RestAreaFacility::getPsName).toList();

        List<RestAreaDetailDto.FoodDto> foods = restAreaFoodRepository.findByStdRestNm(restArea.getStdRestNm())
                .stream().map(f -> new RestAreaDetailDto.FoodDto(f.getFoodNm(), f.getFoodCost()))
                .toList();

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
                .distance(distance)
                .build();
    }
}