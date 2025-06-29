package com.kargobaji.kargobaji.review.repository;

import com.kargobaji.kargobaji.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestAreaId(Long restAreaId);

    @Query("SELECT AVG(r.grade) FROM Review r WHERE r.restArea.id = :restAreaId")
    Double findAverageGradeByRestAreaId(@Param("restAreaId") Long restAreaId);
}
