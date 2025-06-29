package com.kargobaji.kargobaji.search;

import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import com.kargobaji.kargobaji.search.category.CategorySearchService;
import com.kargobaji.kargobaji.search.path.KakaoRouteManager;
import com.kargobaji.kargobaji.search.path.RestAreaSearchService;
import com.kargobaji.kargobaji.search.text.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("rest-area/search")
public class SearchController {

    private final CategorySearchService categorySearchService;
    private final KakaoRouteManager kakaoRouteManager;
    private final RestAreaSearchService restAreaSearchService;
    private final SearchService searchService;

    // 텍스트 검색
    @GetMapping()
    public ResponseEntity<List<RestAreaDetailDto>> search(@RequestParam("keyword") String keyword){
        List<RestAreaDetailDto> result = searchService.searchRestAreas(keyword);
        return ResponseEntity.ok(result);
    }

    // 카테고리 검색
    @GetMapping("/filter")
    public ResponseEntity<List<RestAreaDetailDto>> filterRestAreas(
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> facilities,
            @RequestParam(required = false) List<String> gases,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Double currentLat,
            @RequestParam(required = false) Double currentLng
    ) {
        List<RestAreaDetailDto> result = categorySearchService.getRestAreasByFilter(brands, facilities, gases, page, currentLat, currentLng);
        return ResponseEntity.ok(result);
    }

    // 출발지 -> 도착지 경로 중 반경 1km 이내 휴게소 조회 API
    @GetMapping("/path")
    public ResponseEntity<List<RestAreaDetailDto>> getRestAreaAlongRoute(
            @RequestParam double originX, @RequestParam double originY,
            @RequestParam double destX, @RequestParam double destY
    ){
        // 경로 좌표 조회
        List<double[]> routePoints = kakaoRouteManager.getRoutePoints(originX, originY, destX, destY);

        // 각 경로상의 점에서
        List<RestAreaDetailDto> nearbyRestAreas = restAreaSearchService.findNearbyRestAreasSortedByDistance(routePoints);

        return ResponseEntity.ok(nearbyRestAreas);
    }
}