package com.kargobaji.kargobaji.like;

import com.kargobaji.kargobaji.like.dto.LikeResponse;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    // 좋아요 생성/삭제
    @PostMapping("/{restAreaId}")
    public ResponseEntity<LikeResponse> likeRestArea(
            @PathVariable Long restAreaId,
            @RequestParam Long userId
            ){
        LikeResponse response = likeService.goodRestArea(restAreaId, userId);
        return ResponseEntity.ok(response);
    }

    // 휴게소별 좋아요 수 조회
    @GetMapping("/{restAreaId}")
    public ResponseEntity<Long> CountRestArea(
            @PathVariable Long restAreaId
    ){
        Long response = likeService.countLikeRestArea(restAreaId);
        return ResponseEntity.ok(response);
    }


    // 유저가 좋아요한 휴게소 이름값(stdRestNm) 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RestAreaDetailDto>> getLikeRestAreas(@PathVariable Long userId){
        List<RestAreaDetailDto> restAreas = likeService.getLikeUser(userId);
        return ResponseEntity.ok(restAreas);
    }
}
