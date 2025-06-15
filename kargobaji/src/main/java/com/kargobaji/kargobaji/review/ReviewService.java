package com.kargobaji.kargobaji.review;


import com.kargobaji.kargobaji.User;
import com.kargobaji.kargobaji.UserRepository;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import com.kargobaji.kargobaji.review.dto.ReviewEditRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewListResponseDto;
import com.kargobaji.kargobaji.review.dto.ReviewRequestDto;
import com.kargobaji.kargobaji.review.dto.ReviewResponseDto;
import com.kargobaji.kargobaji.review.entity.Review;
import com.kargobaji.kargobaji.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestAreaRepository restAreaRepository;
    private final UserRepository userRepository;

    // 리뷰 생성
    @Transactional // 트랜잭션 체크
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, String restAreaNm){
        RestArea restArea = restAreaRepository
                .findFirstByRestAreaNmOrderByIdAsc(restAreaNm)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Review review = requestDto.toEntity(restArea, user);
        Review saved = reviewRepository.save(review);
        return ReviewResponseDto.fromEntity(saved);
    }

    // 휴게소 이름으로 리뷰 조회
    @Transactional
    public ReviewListResponseDto getReviewByRestArea(Long restAreaId) {
        List<Review> reviewList = reviewRepository.findByRestAreaId(restAreaId);
        List<ReviewResponseDto> responseDtos = reviewList.stream()
                .map(ReviewResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new ReviewListResponseDto(responseDtos.size(), responseDtos);
    }

    // 리뷰 단일 조회
    @Transactional
    public ReviewResponseDto getReview (Long id){

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        return ReviewResponseDto.fromEntity(review);
    }

    // 전체 리뷰 조회
    @Transactional
    public List<ReviewResponseDto> getReviewAll() {
        List<Review> reviewList = reviewRepository.findAll();
        return reviewList.stream()
                .map(ReviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto editReview(ReviewEditRequestDto reviewEditRequestDto, Long id){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        reviewEditRequestDto.editToEntity(review);
        return ReviewResponseDto.fromEntity(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }
}
