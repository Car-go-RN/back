package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestAreaGas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestAreaGasRepository extends JpaRepository<RestAreaGas, Long> {
    // stdRestNm을 기준으로 데이터를 조회
    RestAreaGas findByStdRestNm(String stdRestNm);
}
