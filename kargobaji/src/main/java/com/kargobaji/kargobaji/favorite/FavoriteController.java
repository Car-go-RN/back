package com.kargobaji.kargobaji.favorite;

import com.kargobaji.kargobaji.favorite.dto.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 생성/삭제
    @PostMapping("/{restAreaId}")
    public ResponseEntity<FavoriteResponse> favoriteRestAreaId(
            @PathVariable Long restAreaId,
            @RequestParam Long userId
    ){
        FavoriteResponse response = favoriteService.selectRestArea(restAreaId, userId);
        return ResponseEntity.ok(response);
    }

    // 유저가 즐겨찾기한 휴게소 id값 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Long>> getFavoriteRestAreaId(@PathVariable Long userId){
        List<Long> restAreaId = favoriteService.getFavoriteUser(userId);
        return ResponseEntity.ok(restAreaId);
    }
}
