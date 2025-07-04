package com.kargobaji.kargobaji.like;

import com.kargobaji.kargobaji.like.dto.LikeResponse;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaIdDto;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    // 좋아요 생성/삭제 ●
    // http://13.124.148.94:8080/likes/529?userId=2
    @PostMapping("/{restAreaId}")
    public ResponseEntity<LikeResponse> likeRestArea(
            @PathVariable Long restAreaId,
            @RequestParam Long userId
            ){
        LikeResponse response = likeService.goodRestArea(restAreaId, userId);
        return ResponseEntity.ok(response);
    }

    // 휴게소별 좋아요 수 조회 ●
    // http://13.124.148.94:8080/likes/509
    @GetMapping("/{restAreaId}")
    public ResponseEntity<Long> CountRestArea(
            @PathVariable Long restAreaId
    ){
        Long response = likeService.countLikeRestArea(restAreaId);
        return ResponseEntity.ok(response);
    }

    // 유저가 좋아요한 휴게소 조회 ●
    // http://13.124.148.94:8080/likes/user/2
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RestAreaDetailDto>> getLikeRestAreas(@PathVariable Long userId){
        List<RestAreaDetailDto> restAreas = likeService.getLikeUser(userId);
        return ResponseEntity.ok(restAreas);
    }

    // 유저가 좋아요한 휴게소 id 조회 ●
    // http://13.124.148.94:8080/likes/check/user/2
    @GetMapping("/check/user/{userId}")
    public ResponseEntity<List<RestAreaIdDto>> getLikeRestAreaId(@PathVariable Long userId){
        List<RestAreaIdDto> restAreaIdDtos = likeService.getLikeUserRestAreaId(userId);
        return ResponseEntity.ok(restAreaIdDtos);
    }
}
