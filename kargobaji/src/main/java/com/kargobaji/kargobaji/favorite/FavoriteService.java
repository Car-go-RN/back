package com.kargobaji.kargobaji.favorite;

import com.kargobaji.kargobaji.User;
import com.kargobaji.kargobaji.UserRepository;
import com.kargobaji.kargobaji.favorite.dto.FavoriteResponse;
import com.kargobaji.kargobaji.favorite.entity.Favorite;
import com.kargobaji.kargobaji.favorite.reposiroty.FavoriteRepository;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import com.kargobaji.kargobaji.openAPI.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RestAreaRepository restAreaRepository;

    // 즐겨찾기 생성/삭제
    @Transactional
    public FavoriteResponse selectRestArea(Long restAreaId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        RestArea restArea = restAreaRepository.findById(restAreaId)
                .orElseThrow(() -> new IllegalArgumentException("휴게소가 존재하지 않습니다."));

        boolean exists = favoriteRepository.existsByUserAndRestArea(user, restArea);

        if(exists){
            favoriteRepository.deleteByUserAndRestArea(user, restArea);
            return FavoriteResponse.builder()
                    .user(user.getId())
                    .restArea(restArea.getId())
                    .restAreaNm(restArea.getRestAreaNm())
                    .message("즐겨찾기 취소")
                    .build();
        }

        Favorite savedFavorite = favoriteRepository.save(
                Favorite.builder()
                        .user(user)
                        .restArea(restArea)
                        .build()
        );

        return FavoriteResponse.fromEntity(savedFavorite, "즐겨찾기 추가 성공");
    }

    // 해당 유저(id)의 즐겨찾기한 휴게소 이름(stdRestNm) 가져오기
    @Transactional
    public List<String> getFavoriteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(favorite -> favorite.getRestArea().getStdRestNm())
                .collect(Collectors.toList());
    }
}
