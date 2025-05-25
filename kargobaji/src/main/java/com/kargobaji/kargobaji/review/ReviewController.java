package com.kargobaji.kargobaji.review;

import com.kargobaji.kargobaji.review.dto.ReviewEditRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto requestDto,
            @RequestParam String restAreaNm){
        ReviewResponseDto responseDto = reviewService.createReview(requestDto, restAreaNm);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 휴게소 이름으로 리뷰 조회
    @GetMapping("/rest-area")
    public ResponseEntity<List<ReviewResponseDto>> getReviewByRestAreaNm(@RequestParam String restAreaNm){
        List<ReviewResponseDto> reviewResponseDtoList = reviewService.getReviewByRestAreaNm(restAreaNm);
        return ResponseEntity.ok(reviewResponseDtoList);
    }

    // 단일 리뷰 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long id){
        ReviewResponseDto review = reviewService.getReview(id);
        return ResponseEntity.ok(review);
    }

    // 전체 리뷰 조회
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReview(){
        List<ReviewResponseDto> reviews = reviewService.getReviewAll();
        return ResponseEntity.ok(reviews);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> editReview(
            @PathVariable Long id,
            @RequestBody ReviewEditRequestDto reviewEditRequestDto){
        ReviewResponseDto reviewResponseDto = reviewService.editReview(reviewEditRequestDto, id);
        return ResponseEntity.ok(reviewResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return ResponseEntity.ok("리뷰 삭제 성공");
    }
}