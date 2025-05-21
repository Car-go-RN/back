package com.kargobaji.kargobaji.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEditRequestDto {
    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;

    @Max(value = 5, message = "벌점은 5점 이하여야 합니다.")
    @Min(value = 1, message = "벌점은 1점 이상여야 합니다.")
    private int grade;

    private LocalDateTime editTime;
}
