package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestAreaLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestAreaLocalRepository extends JpaRepository<RestAreaLocal, Long> {
    RestAreaLocal findByStdRestNm(String stdRestNm);
}
