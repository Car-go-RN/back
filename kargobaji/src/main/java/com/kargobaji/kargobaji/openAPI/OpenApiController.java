package com.kargobaji.kargobaji.openAPI;


import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
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
            @RequestParam(name = "type", defaultValue = "BRAND") OpenApiType apiType,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo
    ) throws UnsupportedEncodingException{
        return openApiManager.fetch(apiType, pageNo);
    }

    // 공공데이터 DB 저장
    @PostMapping("/sync")
    public ResponseEntity<String> fetchAndSaveData() throws ParseException {
            openApiManager.fetchAndSave();
            return ResponseEntity.ok("데이터가 성공적으로 저장되었습니다.");
    }
}






