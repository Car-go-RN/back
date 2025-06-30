package com.kargobaji.kargobaji.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendResponseDto {
    private Long id;
    private String category;
    private String keyword;
    private Long userId;
}
