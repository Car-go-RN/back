package com.kargobaji.kargobaji.review.dto;

import com.kargobaji.kargobaji.review.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// 무분별한 객체 생성 체크
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewRequestDto {
    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;

    @Max(value = 5, message = "벌점은 5점 이하여야 합니다.")
    @Min(value = 1, message = "벌점은 1점 이상여야 합니다.")
    private int grade;

    @NotBlank(message = "존재하지 않는 유저입니다.")
    private String username;

    @NotBlank(message = "존재하지 않는 휴게소입니다.")
    private String restAreaNm;

    public Review toEntity(){
        return Review.builder()
                .content(this.content)
                .grade(this.grade)
                .username(this.username)
                .restAreaNm(this.restAreaNm)
                .build();
    }
}
