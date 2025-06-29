package com.kargobaji.kargobaji.like;

import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaIdDto;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaBrandRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFacilityRepository;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaFoodRepository;
import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.like.dto.LikeResponse;
import com.kargobaji.kargobaji.like.entity.Like;
import com.kargobaji.kargobaji.like.repository.LikeRepository;
import com.kargobaji.kargobaji.loginSignup.repository.UserRepository;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RestAreaRepository restAreaRepository;

    private final RestAreaBrandRepository restAreaBrandRepository;
    private final RestAreaFoodRepository restAreaFoodRepository;
    private final RestAreaFacilityRepository restAreaFacilityRepository;

    // 좋아요 생성 / 삭제
    @Transactional
    public LikeResponse goodRestArea(Long restAreaId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        RestArea restArea = restAreaRepository.findById(restAreaId)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        boolean exists = likeRepository.existsByUserAndRestArea(user, restArea);

        if(exists){
            likeRepository.deleteByUserAndRestArea(user, restArea);
            return LikeResponse.builder()
                    .user(user.getId())
                    .restArea(restArea.getId())
                    .restAreaNm(restArea.getRestAreaNm())
                    .message("좋아요 취소")
                    .build();
        }

        Like savedLike = likeRepository.save(
                Like.builder()
                        .user(user)
                        .restArea(restArea)
                        .build()
        );

        return LikeResponse.fromEntity(savedLike, "좋아요 성공");
    }

//    public Long countLikeRestArea(Long restAreaId){
//        RestArea restArea = restAreaRepository.findById(restAreaId)
//                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));
//
//        return likeRepository.countByRestAreaId(restAreaId);
//    }


    @Transactional
    public List<RestAreaDetailDto> getLikeUser(Long userId){
        // 유저 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 즐겨찾기 목록 조회
        List<Like> likes = likeRepository.findByUserId(userId);

        if(likes.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기한 휴게소가 존재하지 않습니다.");
        }

        // 즐겨찾기된 휴게소들 가져오기
        List<RestArea> restAreas = likes.stream()
                .map(Like::getRestArea)
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

    @Transactional
    public List<RestAreaIdDto> getLikeUserRestAreaId(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 즐겨찾기 목록 조회
        List<Like> likes = likeRepository.findByUserId(userId);

        return likes.stream()
                .map(like -> RestAreaIdDto.builder()
                        .restAreaId(like.getRestArea().getId())
                        .build())
                .toList();
    }
}
