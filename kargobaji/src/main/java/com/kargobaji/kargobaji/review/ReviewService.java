package com.kargobaji.kargobaji.review;


import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.loginSignup.repository.UserRepository;
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
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, String restAreaNm) {
        if (requestDto.getUserId() == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (requestDto.getGrade() < 1 || requestDto.getGrade() > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이의 값으로 지정해주세요.");
        }

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
        if (reviewEditRequestDto.getUserId() == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        User user = userRepository.findById(reviewEditRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        Long requestUserId = reviewEditRequestDto.getUserId();
        if (!review.getUser().getId().equals(requestUserId)) {
            throw new SecurityException("본인의 리뷰만 수정할 수 있습니다.");
        }

        if (reviewEditRequestDto.getGrade() < 1 || reviewEditRequestDto.getGrade() > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이의 값으로 지정해주세요.");
        }

        reviewEditRequestDto.editToEntity(review);
        return ReviewResponseDto.fromEntity(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }
}
