package com.kargobaji.kargobaji.openAPI.distance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/distance")
public class DistanceController {
    private final DistanceService distanceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDistance(
            @RequestParam("currentLat") double currentLat,
            @RequestParam("currentLng") double currentLng,
            @RequestParam("stdRestNm") String stdRestNm
    ){
        Map<String, Object> response = distanceService.calculateDistance(currentLat, currentLng, stdRestNm);
        return ResponseEntity.ok(response);
    }
}

