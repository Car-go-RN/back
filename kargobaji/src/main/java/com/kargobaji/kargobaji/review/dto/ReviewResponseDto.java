package com.kargobaji.kargobaji.review.dto;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;

    private String content;

    private int grade;

    private LocalDateTime createTime;
    private LocalDateTime editTime;

    private Long userId;

    private Long restAreaId;

    public static ReviewResponseDto fromEntity(Review review){
        return new ReviewResponseDto(
                review.getId(),
                review.getContent(),
                review.getGrade(),
                review.getCreateTime(),
                review.getEditTime(),
                review.getUser().getId(),
                review.getRestArea().getId()
        );

    }
}

