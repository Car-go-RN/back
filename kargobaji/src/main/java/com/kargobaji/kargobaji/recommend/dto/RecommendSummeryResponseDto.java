package com.kargobaji.kargobaji.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendSummeryResponseDto {
    private Long userId;
    private Map<String, List<String>> preferences;
}
