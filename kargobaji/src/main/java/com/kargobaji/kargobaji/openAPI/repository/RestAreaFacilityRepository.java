package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestAreaFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestAreaFacilityRepository extends JpaRepository<RestAreaFacility, Long> {
    // stdRestNm과 psName을 기준으로 데이터를 조회
    RestAreaFacility findByStdRestNmAndPsName(String stdRestNm, String psName);
}