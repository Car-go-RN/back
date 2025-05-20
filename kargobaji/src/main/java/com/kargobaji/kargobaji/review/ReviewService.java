package com.kargobaji.kargobaji.review;


import com.kargobaji.kargobaji.review.dto.ReviewEditRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewResponseDto;
import com.kargobaji.kargobaji.review.entity.Review;
import com.kargobaji.kargobaji.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    // 리뷰 추가
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto){
        Review reviews = reviewRequestDto.toEntity();

        Review saved = reviewRepository.save(reviews);
        return ReviewResponseDto.fromEntity(saved);
    }

    // 리뷰 수정
    public ReviewResponseDto updateReview(Long id, ReviewEditRequestDto reviewEditRequestDto){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        review.setContent(reviewEditRequestDto.getContent());
        review.setGrade(reviewEditRequestDto.getGrade());
        review.setEditTime(LocalDateTime.now());

        reviewRepository.save(review);
        return ReviewResponseDto.fromEntity(review);
    }

    // 리뷰 전체 조회
    public List<Review> getReviewAll(){
        return reviewRepository.findAll();
    }

    // 휴게소 리뷰 전체 조회
    public List<ReviewResponseDto> getReviewByRestArea(String restAreaNm){
        List<Review> reviewList = reviewRepository.findByRestAreaNm(restAreaNm);
        return reviewList.stream().map(ReviewResponseDto::fromEntity).collect(Collectors.toList());
    }

    // 리뷰 조회
    public ReviewResponseDto getReview(Long id){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        return ReviewResponseDto.fromEntity(review);
    }

    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }




}

