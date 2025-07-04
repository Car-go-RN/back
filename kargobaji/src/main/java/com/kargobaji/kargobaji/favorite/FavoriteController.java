package com.kargobaji.kargobaji.favorite;

import com.kargobaji.kargobaji.favorite.dto.FavoriteResponse;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.openAPI.dto.RestAreaIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 생성/삭제 ●
    // http://13.124.148.94:8080/favorites/666?userId=2
    @PostMapping("/{restAreaId}")
    public ResponseEntity<FavoriteResponse> favoriteRestAreaId(
            @PathVariable Long restAreaId,
            @RequestParam Long userId
    ){
        FavoriteResponse response = favoriteService.selectRestArea(restAreaId, userId);
        return ResponseEntity.ok(response);
    }

    // 유저가 즐겨찾기한 휴게소 조회 ●
    // http://13.124.148.94:8080/favorites/user/2
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RestAreaDetailDto>> getFavoriteRestAreas(@PathVariable Long userId){
        List<RestAreaDetailDto> restAreas = favoriteService.getFavoriteUser(userId);
        return ResponseEntity.ok(restAreas);
    }

    // 유저가 즐겨찾기한 휴게소 Id값만 조회 ●
    // http://13.124.148.94:8080/favorites/check/user/2
    @GetMapping("/check/user/{userId}")
    public ResponseEntity<List<RestAreaIdDto>> getLikeRestAreaId(@PathVariable Long userId){
        List<RestAreaIdDto> restAreaIdDtos = favoriteService.getFavoriteUserRestAreaId(userId);
        return ResponseEntity.ok(restAreaIdDtos);
    }
}
