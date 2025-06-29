package com.kargobaji.kargobaji.search.text;

import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RestAreaRepository restAreaRepository;
    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;

    @Transactional(readOnly = true)
    public List<RestAreaDetailDto> searchRestAreas(String keyword, int page){
        int pageSize = 15;
        int offset = (Math.max(page, 1) - 1) * pageSize;

        List<RestArea> restAreas = restAreaRepository.searchByKeyword(keyword);

        List<RestArea> paginated = restAreas.stream()
                .skip(offset)
                .limit(pageSize)
                .toList();

        return paginated.stream()
                .map(ra -> {
                    List<String> brands = restAreaBrandRepository.findByStdRestNm(ra.getStdRestNm())
                            .stream()
                            .map(RestAreaBrand::getBrdName)
                            .toList();

                    List<String> facilities = restAreaFacilityRepository.findByStdRestNm(ra.getStdRestNm())
                            .stream()
                            .map(RestAreaFacility::getPsName)
                            .toList();

                    List<RestAreaDetailDto.FoodDto> foods = restAreaFoodRepository.findByStdRestNm(ra.getStdRestNm())
                            .stream()
                            .map(f -> new RestAreaDetailDto.FoodDto(f.getFoodNm(), f.getFoodCost()))
                            .toList();

                    return RestAreaDetailDto.builder()
                            .id(ra.getId())
                            .stdRestNm(ra.getStdRestNm())
                            .restAreaNm(ra.getRestAreaNm())
                            .reviewAVG(ra.getReviewAVG())
                            .gasolinePrice(ra.getGasolinePrice())
                            .diselPrice(ra.getDiselPrice())
                            .lpgPrice(ra.getLpgPrice())
                            .electric(ra.getElectric())
                            .hydrogen(ra.getHydrogen())
                            .roadAddress(ra.getRoadAddress())
                            .phone(ra.getPhone())
                            .latitude(ra.getLatitude())
                            .longitude(ra.getLongitude())
                            .brands(brands)
                            .facilities(facilities)
                            .foods(foods)
                            .build();

                })
                .collect(Collectors.toList());
    }


}
