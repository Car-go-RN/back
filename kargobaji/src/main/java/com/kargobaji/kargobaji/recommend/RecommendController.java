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

    // 설문조사한 결과 저장 ●
    /* http://13.124.148.94:8080/recommend/save/2
    * {
            "preferences" : {
                "gases" : ["전기", "수소"],
                "facilities" : ["수유실"],
                "brands" : ["CU", "GS25"]
            }
        }
    * */
    @PostMapping("/save/{userId}")
    public ResponseEntity<List<RecommendResponseDto>> recommendUser(
            @PathVariable Long userId,
            @RequestBody RecommendRequestDto recommendRequestDtos
            ){
        return recommendService.recommendUser(userId, recommendRequestDtos);
    }

    // 유저의 설문조사에서 선택한 키워드 조회
    // http://13.124.148.94:8080/recommend/list/2 ●
    @GetMapping("/list/{userId}")
    public ResponseEntity<RecommendSummeryResponseDto> recommendCategoryByUser(@PathVariable Long userId){
        return recommendService.getRecommendationsByUserId(userId);
    }
}
