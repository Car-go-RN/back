package com.kargobaji.kargobaji.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReviewListResponseDto {
    private int count;
    private List<ReviewResponseDto> reviews;
}
