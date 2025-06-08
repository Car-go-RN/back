package com.kargobaji.kargobaji.likeAndFavorite.repository;

import com.kargobaji.kargobaji.User;
import com.kargobaji.kargobaji.likeAndFavorite.entity.Like;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    void deleteByUserAndRestArea(User user, RestArea restArea);

    long countByRestAreaId(Long restAreaId);

}
