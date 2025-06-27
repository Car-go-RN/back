package com.kargobaji.kargobaji.openAPI.repository;

import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestAreaRepository extends JpaRepository<RestArea, Long> {
    // 휴게소 정보 조회
    List<RestArea> findByRestAreaNmContaining(String restAreaNm);

    Optional<RestArea> findByStdRestNm(String stdRestNm);

    // 중복된 휴게소 이름일 경우엔 id값이 가장 작은 데이터를 조회
    Optional<RestArea> findFirstByRestAreaNmOrderByIdAsc(String restAreaNm);

    // 전체 조회
    @Query("SELECT r FROM RestArea r")
    List<RestArea> findAllRestAreas();

    // 브랜드만 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT b.stdRestNm FROM RestAreaBrand b
            WHERE b.brdName IN :brands
        )
    """)
    List<RestArea> findByBrands(@Param("brands") List<String> brands);

    // 편의시설만 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT f.stdRestNm FROM RestAreaFacility f
            WHERE f.psName IN :facilities
        )
    """)
    List<RestArea> findByFacilities(@Param("facilities") List<String> facilities);

    // 브랜드 + 편의시설 모두 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT b.stdRestNm FROM RestAreaBrand b WHERE b.brdName IN :brands
        )
        AND r.stdRestNm IN (
            SELECT f.stdRestNm FROM RestAreaFacility f WHERE f.psName IN :facilities
        )
    """)
    List<RestArea> findByBrandsAndFacilities(
            @Param("brands") List<String> brands,
            @Param("facilities") List<String> facilities
    );

    // 전기, 수소, LPG 조건 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE (:hasElectric IS NULL OR r.electric = 'O')
          AND (:hasHydrogen IS NULL OR r.hydrogen = 'O')
          AND (:hasLpg IS NULL OR r.lpgPrice IS NOT NULL)
    """)
    List<RestArea> findByGases(@Param("hasElectric") Boolean hasElectric,
                               @Param("hasHydrogen") Boolean hasHydrogen,
                               @Param("hasLpg") Boolean hasLpg);
}