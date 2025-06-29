package com.kargobaji.kargobaji.favorite;

import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaIdDto;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.favorite.dto.FavoriteResponse;
import com.kargobaji.kargobaji.favorite.entity.Favorite;
import com.kargobaji.kargobaji.favorite.reposiroty.FavoriteRepository;
import com.kargobaji.kargobaji.loginSignup.repository.UserRepository;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RestAreaRepository restAreaRepository;

    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;

    // 즐겨찾기 생성/삭제
    @Transactional
    public FavoriteResponse selectRestArea(Long restAreaId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        RestArea restArea = restAreaRepository.findById(restAreaId)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        boolean exists = favoriteRepository.existsByUserAndRestArea(user, restArea);

        if(exists){
            favoriteRepository.deleteByUserAndRestArea(user, restArea);
            return FavoriteResponse.builder()
                    .user(user.getId())
                    .restArea(restArea.getId())
                    .restAreaNm(restArea.getRestAreaNm())
                    .message("즐겨찾기 취소")
                    .build();
        }

        Favorite savedFavorite = favoriteRepository.save(
                Favorite.builder()
                        .user(user)
                        .restArea(restArea)
                        .build()
        );

        return FavoriteResponse.fromEntity(savedFavorite, "즐겨찾기 추가 성공");
    }

    // 해당 유저(id)의 즐겨찾기한 휴게소 가져오기
    @Transactional
    public List<RestAreaDetailDto> getFavoriteUser(Long userId){
        // 유저 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 즐겨찾기 목록 조회
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        // 즐겨찾기된 휴게소들 가져오기
        List<RestArea> restAreas = favorites.stream()
                .map(Favorite::getRestArea)
                .toList();

        // 각 휴게소에 대한 상세 정보 생성
        return restAreas.stream().map(restArea -> {
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
                    .build();
        }).toList();
    }

    public List<RestAreaIdDto> getFavoriteUserRestAreaId(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(favorite -> RestAreaIdDto.builder()
                        .restAreaId(favorite.getRestArea().getId())
                        .build())
                .toList();
    }
}
