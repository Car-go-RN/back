package com.kargobaji.kargobaji.local.Address;

import com.kargobaji.kargobaji.local.Address.dto.KakaoPlaceResponse;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api")
public class AddressController {

    private final RestAreaLocationService restAreaLocationService;

    // 검색 조회 (직접)
    @GetMapping("/kakao/search")
    public ResponseEntity<?> search(@RequestParam String stdRestNm){
        KakaoPlaceResponse.Document result = restAreaLocationService.getPlaceInfo(stdRestNm);
        return ResponseEntity.ok(result);
    }

    // kakao map 검색 조회
    @GetMapping("/kakao/search/list")
    public ResponseEntity<List<KakaoPlaceResponse.Document>> getSearchList(){
        List<KakaoPlaceResponse.Document> results = restAreaLocationService.getAllPlacesInfo();
        return ResponseEntity.ok(results);
    }

    // 휴게소 위치 정보 저장
    @PostMapping("/kakao/update")
    public ResponseEntity<String> updateAllLocationInfo(){
        restAreaLocationService.updateAllRestAreaLocationInfo();
        return ResponseEntity.ok("위치 정보 업데이트 성공");
    }
}
