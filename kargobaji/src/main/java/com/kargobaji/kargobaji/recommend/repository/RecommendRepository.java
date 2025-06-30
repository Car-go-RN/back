package com.kargobaji.kargobaji.recommend.repository;

import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.recommend.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    void deleteByUser(User user);
    List<Recommend> findByUser(User user);
}
