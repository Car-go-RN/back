package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestAreaRepository extends JpaRepository<RestArea, Long> {
    // 휴게소 정보 조회
    List<RestArea> findByRestAreaNmContaining(String restAreaNm);

    // 중복된 휴게소 이름일 경우엔 id값이 가장 작은 데이터를 조회
    Optional<RestArea> findFirstByRestAreaNmOrderByIdAsc(String restAreaNm);
}
