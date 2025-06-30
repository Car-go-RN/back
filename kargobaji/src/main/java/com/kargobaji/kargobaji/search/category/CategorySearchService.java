package com.kargobaji.kargobaji.search.category;

import com.kargobaji.kargobaji.openAPI.distance.DistanceService;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFood;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategorySearchService {

    private final RestAreaRepository restAreaRepository;
    private final RestAreaBrandRepository brandRepository;
    private final RestAreaFacilityRepository facilityRepository;
    private final RestAreaFoodRepository foodRepository;

    private final DistanceService distanceService;

    public List<RestAreaDetailDto> getRestAreasByFilter(List<String> brands, List<String> facilities, List<String> gases,
                                                        int page, Double currentLat, Double currentLng) {
        int pageSize = 15;
        int offset = (Math.max(page, 1) - 1) * pageSize;

        List<RestArea> filtered;

        boolean hasBrands = brands != null && !brands.isEmpty();
        boolean hasFacilities = facilities != null && !facilities.isEmpty();
        boolean hasGases = gases != null && !gases.isEmpty();

        // gases 리스트 파싱
        Boolean hasElectric = null;
        Boolean hasHydrogen = null;
        Boolean hasLpg = null;
        if (hasGases) {
            hasElectric = gases.contains("전기") ? true : null;
            hasHydrogen = gases.contains("수소") ? true : null;
            hasLpg = gases.contains("lpg") ? true : null;
        }

        // [1] a X, b X, c X → 전체
        if (!hasBrands && !hasFacilities && !hasGases) {
            filtered = restAreaRepository.findAllRestAreas();
        }

        // [2] a O, b X, c X
        else if (hasBrands && !hasFacilities && !hasGases) {
            filtered = restAreaRepository.findByBrands(brands, brands.size());
        }

        // [3] a X, b O, c X
        else if (!hasBrands && hasFacilities && !hasGases) {
            filtered = restAreaRepository.findByFacilities(facilities, facilities.size());
        }

        // [4] a X, b X, c O
        else if (!hasBrands && !hasFacilities && hasGases) {
            filtered = restAreaRepository.findByGases(hasElectric, hasHydrogen, hasLpg);
        }

        // [5] a O, b O, c X
        else if (hasBrands && hasFacilities && !hasGases) {
            filtered = restAreaRepository.findByBrandsAndFacilities(brands, brands.size(), facilities, facilities.size());
        }

        // [6] a O, b X, c O
        else if (hasBrands && !hasFacilities && hasGases) {
            final Boolean electric = hasElectric;
            final Boolean hydrogen = hasHydrogen;
            final Boolean lpg = hasLpg;
            filtered = restAreaRepository.findByBrands(brands, brands.size()).stream()
                    .filter(r -> matchGases(r, electric, hydrogen, lpg))
                    .toList();
        }

        // [7] a X, b O, c O
        else if (!hasBrands && hasFacilities && hasGases) {
            final Boolean electric = hasElectric;
            final Boolean hydrogen = hasHydrogen;
            final Boolean lpg = hasLpg;
            filtered = restAreaRepository.findByFacilities(facilities, facilities.size()).stream()
                    .filter(r -> matchGases(r, electric, hydrogen, lpg))
                    .toList();
        }

        // [8] a O, b O, c O
        else {
            final Boolean electric = hasElectric;
            final Boolean hydrogen = hasHydrogen;
            final Boolean lpg = hasLpg;
            filtered = restAreaRepository.findByBrandsAndFacilities(brands, brands.size(), facilities, facilities.size()).stream()
                    .filter(r -> matchGases(r, electric, hydrogen, lpg))
                    .toList();
        }

        // 결과를 DTO로 변환 + 페이지네이션 적용
        return filtered.stream()
                .skip(offset)
                .limit(pageSize)
                .map(restArea -> {
                    List<String> brandList = brandRepository.findByStdRestNm(restArea.getStdRestNm())
                            .stream().map(RestAreaBrand::getBrdName).toList();

                    List<String> facilityList = facilityRepository.findByStdRestNm(restArea.getStdRestNm())
                            .stream().map(RestAreaFacility::getPsName).toList();

                    List<RestAreaFood> foodList = foodRepository.findByStdRestNm(restArea.getStdRestNm());
                    List<RestAreaDetailDto.FoodDto> foodDtos = foodList.stream()
                            .map(food -> new RestAreaDetailDto.FoodDto(food.getFoodNm(), food.getFoodCost()))
                            .toList();

                    String distanceStr = null;
                    try{
                        Map<String, Object> distInfo = distanceService.calculateDistance(currentLat, currentLng, restArea.getStdRestNm());
                        Integer distanceKm = (Integer) distInfo.get("distanceKm");
                        double rawKm = distanceKm.doubleValue();

                        if(rawKm < 1.0){
                            distanceStr = String.format("%.1fkm", rawKm);
                        }
                        else {
                            distanceStr = String.format("%.0fkm", rawKm);
                        }
                    }
                    catch (Exception e){
                        throw new RuntimeException("거리 계산 실패 : " + e.getMessage());
                    }

                    return RestAreaDetailDto.builder()
                            .id(restArea.getId())
                            .stdRestNm(restArea.getStdRestNm())
                            .reviewAVG(restArea.getReviewAVG())
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
                            .brands(brandList)
                            .facilities(facilityList)
                            .foods(foodDtos)
                            .distance(distanceStr)
                            .build();
                })
                .toList();
    }


        private boolean matchGases(RestArea r, Boolean electric, Boolean hydrogen, Boolean lpg) {
        return (electric == null || "O".equals(r.getElectric())) &&
                (hydrogen == null || "O".equals(r.getHydrogen())) &&
                (lpg == null || r.getLpgPrice() != null);
    }



}
