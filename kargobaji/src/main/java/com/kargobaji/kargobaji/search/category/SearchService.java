package com.kargobaji.kargobaji.search.category;

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

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RestAreaRepository restAreaRepository;
    private final RestAreaBrandRepository brandRepository;
    private final RestAreaFacilityRepository facilityRepository;
    private final RestAreaFoodRepository foodRepository;

    public List<RestAreaDetailDto> getRestAreasByFilter(List<String> brands, List<String> facilities) {
        List<RestArea> filtered;

        if ((brands == null || brands.isEmpty()) && (facilities == null || facilities.isEmpty())) {
            filtered = restAreaRepository.findAllRestAreas();
        } else if (brands == null || brands.isEmpty()) {
            filtered = restAreaRepository.findByFacilities(facilities);
        } else if (facilities == null || facilities.isEmpty()) {
            filtered = restAreaRepository.findByBrands(brands);
        } else {
            filtered = restAreaRepository.findByBrandsAndFacilities(brands, facilities);
        }

        return filtered.stream().map(restArea -> {
            List<String> brandList = brandRepository.findByStdRestNm(restArea.getStdRestNm())
                    .stream().map(RestAreaBrand::getBrdName).toList();

            List<String> facilityList = facilityRepository.findByStdRestNm(restArea.getStdRestNm())
                    .stream().map(RestAreaFacility::getPsName).toList();

            List<RestAreaFood> foodList = foodRepository.findByStdRestNm(restArea.getStdRestNm());
            List<RestAreaDetailDto.FoodDto> foodDtos = foodList.stream()
                    .map(food -> new RestAreaDetailDto.FoodDto(food.getFoodNm(), food.getFoodCost()))
                    .toList();

            return RestAreaDetailDto.builder()
                    .id(restArea.getId())
                    .stdRestNm(restArea.getStdRestNm())
                    .gasolinePrice(restArea.getGasolinePrice())
                    .diselPrice(restArea.getDiselPrice())
                    .lpgPrice(restArea.getLpgPrice())
                    .roadAddress(restArea.getRoadAddress())
                    .phone(restArea.getPhone())
                    .latitude(restArea.getLatitude())
                    .longitude(restArea.getLongitude())
                    .restAreaNm(restArea.getRestAreaNm())
                    .brands(brandList)
                    .facilities(facilityList)
                    .foods(foodDtos)
                    .build();
        }).toList();
    }

}
