package com.kargobaji.kargobaji.recommend;

import com.kargobaji.kargobaji.recommend.dto.RecommendRequestDto;
import com.kargobaji.kargobaji.recommend.dto.RecommendResponseDto;
import com.kargobaji.kargobaji.recommend.dto.RecommendSummeryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    // 설문조사한 결과 저장
    @PostMapping("/save/{userId}")
    public ResponseEntity<List<RecommendResponseDto>> recommendUser(
            @PathVariable Long userId,
            @RequestBody RecommendRequestDto recommendRequestDtos
            ){
        return recommendService.recommendUser(userId, recommendRequestDtos);
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<RecommendSummeryResponseDto> recommendCategoryByUser(@PathVariable Long userId){
        return recommendService.getRecommendationsByUserId(userId);
    }
}
