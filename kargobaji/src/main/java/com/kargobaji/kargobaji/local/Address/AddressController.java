package com.kargobaji.kargobaji.local.Address;

import com.kargobaji.kargobaji.local.Address.dto.KakaoPlaceResponse;
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

    // 검색 조회
    @GetMapping("/kakao/search/list")
    public ResponseEntity<List<KakaoPlaceResponse.Document>> getSearchList(){
        List<KakaoPlaceResponse.Document> results = restAreaLocationService.getAllPlacesInfo();
        return ResponseEntity.ok(results);
    }

}
