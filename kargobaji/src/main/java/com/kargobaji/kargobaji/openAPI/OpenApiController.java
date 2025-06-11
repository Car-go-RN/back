package com.kargobaji.kargobaji.openAPI;

import com.kargobaji.kargobaji.openAPI.dto.RestAreaDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api")
public class OpenApiController {
    private final OpenApiManager openApiManager;

    // 공공데이터 호출
    @GetMapping
    public ResponseEntity<Map> fetch(
            @RequestParam(name = "type", defaultValue = "BRAND") OpenApiType apiType, // 가져올 데이터 종류
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo // 페이지 번호
    ) {
        return openApiManager.fetch(apiType, pageNo);
    }

    // 공공데이터 DB 저장
    @PostMapping("/sync")
    public ResponseEntity<String> fetchAndSaveData(){
            openApiManager.fetchAndSave();
            return ResponseEntity.ok("데이터가 성공적으로 저장되었습니다.");
    }

    // 분야별 조회
    @GetMapping("/{table}") // brand, facility, food, gas
    public ResponseEntity<List<Map<String, Object>>> getData(
            @PathVariable String table,
            @RequestParam(required = false) List<String> field, // 추출할 필드 (field=필드이름) * 여러개 할시 , 붙이기
            @RequestParam(required = false) Map<String, String> filters, // 필터링 (필드이름=필터링할 내용)
            @RequestParam(required = false) Integer limit // 개수 제한 (limit=개수)
    ){
        // 필터링할 시 field와 limit 삭제
        // -> applyFilters가 없는 필드를 찾을 수도 있음.
        // -> 필터링이 잘못 적용되지 않도록 하기 위해서.
        if(filters != null){
            filters.remove("field");
            filters.remove("limit");
        }

        List<Map<String, Object>> result = openApiManager.getData(table, field, filters, limit);
        return ResponseEntity.ok(result);
    }

    // 휴게소 상세 정보 조회
    @GetMapping("/detail")
    public ResponseEntity<RestAreaDetailDto> getRestAreaDetail(@RequestParam String stdRestNm){
        RestAreaDetailDto detailDto = openApiManager.getRestAreaDetail(stdRestNm);
        return ResponseEntity.ok(detailDto);
    }
}






