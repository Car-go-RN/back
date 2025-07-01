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

    // 텍스트 검색 ●
    // http://13.124.148.94:8080/rest-area/search?currentLat=37.572950&currentLng=126.979357&keyword=고성&page=1
    @GetMapping()
    public ResponseEntity<List<RestAreaDetailDto>> search(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "1") int page){
        List<RestAreaDetailDto> result = searchService.searchRestAreas(keyword, page);
        return ResponseEntity.ok(result);
    }

    // 카테고리 검색 ●
    //http://13.124.148.94:8080/rest-area/search/filter?currentLat=37.572950&currentLng=126.979357&brands=CU&facilities=ATM&gases=수소&page=1
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

    // 출발지 -> 도착지 경로 중 반경 0.35km 이내 휴게소 조회 API ●
    // http://13.124.148.94:8080/rest-area/search/path?originX=126.979357&originY=37.572950&destX=129.059210&destY=35.157600&page=2
    @GetMapping("/path")
    public ResponseEntity<List<RestAreaDetailDto>> getRestAreaAlongRoute(
            @RequestParam double originX, @RequestParam double originY,
            @RequestParam double destX, @RequestParam double destY,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int limit
    ){
        // 경로 좌표 조회
        List<double[]> routePoints = kakaoRouteManager.getRoutePoints(originX, originY, destX, destY);

        // 각 경로상의 점에서
        List<RestAreaDetailDto> nearbyRestAreas = restAreaSearchService.findNearbyRestAreasSortedByDistance(routePoints, page, limit);

        return ResponseEntity.ok(nearbyRestAreas);
    }
}