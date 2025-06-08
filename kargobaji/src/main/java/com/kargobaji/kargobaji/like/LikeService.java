package com.kargobaji.kargobaji.like;

import com.kargobaji.kargobaji.User;
import com.kargobaji.kargobaji.UserRepository;
import com.kargobaji.kargobaji.like.dto.LikeResponse;
import com.kargobaji.kargobaji.like.entity.Like;
import com.kargobaji.kargobaji.like.repository.LikeRepository;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final RestAreaRepository restAreaRepository;

    // 좋아요 생성 / 삭제
    @Transactional
    public LikeResponse goodRestArea(Long restAreaId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        RestArea restArea = restAreaRepository.findById(restAreaId)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        boolean exists = likeRepository.existsByUserAndRestArea(user, restArea);

        if(exists){
            likeRepository.deleteByUserAndRestArea(user, restArea);
            return LikeResponse.builder()
                    .user(user.getId())
                    .restArea(restArea.getId())
                    .restAreaNm(restArea.getRestAreaNm())
                    .message("좋아요 취소")
                    .build();
        }

        Like savedLike = likeRepository.save(
                Like.builder()
                        .user(user)
                        .restArea(restArea)
                        .build()
        );

        return LikeResponse.fromEntity(savedLike, "좋아요 성공");
    }

    @Transactional
    public Long countLikeRestArea(Long restAreaId){
        RestArea restArea = restAreaRepository.findById(restAreaId)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        return likeRepository.countByRestAreaId(restAreaId);
    }
}
