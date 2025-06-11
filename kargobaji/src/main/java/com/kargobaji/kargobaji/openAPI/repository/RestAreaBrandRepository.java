package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestAreaBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestAreaBrandRepository extends JpaRepository<RestAreaBrand, Long> {
    // stdRestNm과 brdName 기준으로 데이터를 조회
    RestAreaBrand findByStdRestNmAndBrdName(String stdRestNm, String brdName);

    List<RestAreaBrand> findByStdRestNm(String stdRestNm);
}
