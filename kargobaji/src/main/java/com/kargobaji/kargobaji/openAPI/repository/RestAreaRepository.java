package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestAreaRepository extends JpaRepository<RestArea, Long> {
    // stdRestNm을 기준으로 데이터를 조회
    List<RestArea> findByRestAreaNmContaining(String restAreaNm);
}
