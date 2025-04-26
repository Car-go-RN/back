package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestAreaFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestAreaFoodRepository extends JpaRepository<RestAreaFood, Long> {
    // stdRestNm과 foodNm을 기준으로 데이터를 조회
    RestAreaFood findByStdRestNmAndFoodNm(String stdRestNm, String foodNm);
}
