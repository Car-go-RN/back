package com.kargobaji.kargobaji.favorite.reposiroty;

import com.kargobaji.kargobaji.User;
import com.kargobaji.kargobaji.favorite.entity.Favorite;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    void deleteByUserAndRestArea(User user, RestArea restArea);

    boolean existsByUserAndRestArea(User user, RestArea restArea);

    List<Favorite> findByUserId(Long userId);
}