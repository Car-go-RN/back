package com.kargobaji.kargobaji.openAPI;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-api")
public class OpenApiController {
    private final OpenApiManager openApiManager;

    // 공공데이터 호출
    @GetMapping
    public ResponseEntity<?> fetch() throws UnsupportedEncodingException{
        return ResponseEntity.ok(openApiManager.fetch().getBody());
    }

    // 공공데이터 DB 저장






}
