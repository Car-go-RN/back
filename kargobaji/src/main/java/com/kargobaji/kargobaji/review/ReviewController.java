package com.kargobaji.kargobaji.review;

import com.kargobaji.kargobaji.review.dto.ReviewEditRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewResponseDto;
import com.kargobaji.kargobaji.review.entity.Review;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 게시
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody @Valid ReviewRequestDto reviewRequestDto){
        return ResponseEntity.ok(reviewService.createReview(reviewRequestDto));
    }

    // 리뷰 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<Review>> getReviewAll(){
        return ResponseEntity.ok(reviewService.getReviewAll());
    }

    // 휴게소 리뷰 조회
    @GetMapping("/search")
    public ResponseEntity<List<ReviewResponseDto>> getReviewByRestAreaNm(@RequestParam("restAreaNm") String restAreaNm){
        List<ReviewResponseDto> reviewResponseDtos = reviewService.getReviewByRestArea(restAreaNm);
        return ResponseEntity.ok(reviewResponseDtos);
    }

    // 리뷰 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long id){
        ReviewResponseDto reviewResponseDto = reviewService.getReview(id);
        return ResponseEntity.ok(reviewResponseDto);
    }

    // 리뷰 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long id, @RequestBody @Valid ReviewEditRequestDto reviewEditRequestDto){
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(id, reviewEditRequestDto);
        return ResponseEntity.ok(reviewResponseDto);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return ResponseEntity.ok("리뷰 삭제 성공");
    }
}
