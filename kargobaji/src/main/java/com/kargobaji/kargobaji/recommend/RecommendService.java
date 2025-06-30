package com.kargobaji.kargobaji.recommend;

import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.loginSignup.repository.UserRepository;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.recommend.dto.RecommendRequestDto;
import com.kargobaji.kargobaji.recommend.dto.RecommendResponseDto;
import com.kargobaji.kargobaji.recommend.dto.RecommendSummeryResponseDto;
import com.kargobaji.kargobaji.recommend.entity.Recommend;
import com.kargobaji.kargobaji.recommend.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final UserRepository userRepository;

    //추천 키워드 저장
    @Transactional
    public ResponseEntity<List<RecommendResponseDto>> recommendUser(Long userId, RecommendRequestDto requestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 기존 추천 정보 삭제
        recommendRepository.deleteByUser(user);

        List<Recommend> save = new ArrayList<>();
        requestDto.getPreferences().forEach((category, keywords) -> {
            for(String keyword : keywords){
                save.add(Recommend.builder()
                        .category(category)
                        .keyword(keyword)
                        .user(user)
                        .build());
            }
        });

        List<Recommend> saved = recommendRepository.saveAll(save);

        // dto 변환
        List<RecommendResponseDto> responseDtos = saved.stream()
                .map(r -> RecommendResponseDto.builder()
                        .id(r.getId())
                        .category(r.getCategory())
                        .keyword(r.getKeyword())
                        .userId(r.getUser().getId())
                        .build())
                .toList();

        return ResponseEntity.ok(responseDtos);
    }


    // 개별 사용자 추천 정보(키워드) 조회
    @Transactional
    public ResponseEntity<RecommendSummeryResponseDto> getRecommendationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        List<Recommend> recommends = recommendRepository.findByUser(user);

        Map<String, List<String>> responseDtos = recommends.stream()
                .collect(Collectors.groupingBy(
                        Recommend::getCategory,
                        Collectors.mapping(Recommend::getKeyword, Collectors.toList())
                ));

        RecommendSummeryResponseDto summeryResponseDto = RecommendSummeryResponseDto.builder()
                .userId(userId)
                .preferences(responseDtos)
                .build();

        return ResponseEntity.ok(summeryResponseDto);
    }
}
