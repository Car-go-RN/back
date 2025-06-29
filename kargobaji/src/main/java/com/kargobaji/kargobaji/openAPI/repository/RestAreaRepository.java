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

    Optional<RestArea> findFirstByRestAreaNmOrderByIdAsc(String restAreaNm);

    // 전체 조회
    @Query("SELECT r FROM RestArea r")
    List<RestArea> findAllRestAreas();

    // 브랜드 리스트 내 모든 브랜드를 포함하는 휴게소만 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT b.stdRestNm FROM RestAreaBrand b
            WHERE b.brdName IN :brands
            GROUP BY b.stdRestNm
            HAVING COUNT(DISTINCT b.brdName) = :brandsCount
        )
    """)
    List<RestArea> findByBrands(
            @Param("brands") List<String> brands,
            @Param("brandsCount") long brandsCount);

    // 편의시설 리스트 내 모든 편의시설을 포함하는 휴게소만 필터
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT f.stdRestNm FROM RestAreaFacility f
            WHERE f.psName IN :facilities
            GROUP BY f.stdRestNm
            HAVING COUNT(DISTINCT f.psName) = :facilitiesCount
        )
    """)
    List<RestArea> findByFacilities(
            @Param("facilities") List<String> facilities,
            @Param("facilitiesCount") long facilitiesCount);

    // 브랜드 + 편의시설 모두 필터 (각각 모든 항목 포함)
    @Query("""
        SELECT r FROM RestArea r
        WHERE r.stdRestNm IN (
            SELECT b.stdRestNm FROM RestAreaBrand b
            WHERE b.brdName IN :brands
            GROUP BY b.stdRestNm
            HAVING COUNT(DISTINCT b.brdName) = :brandsCount
        )
        AND r.stdRestNm IN (
            SELECT f.stdRestNm FROM RestAreaFacility f
            WHERE f.psName IN :facilities
            GROUP BY f.stdRestNm
            HAVING COUNT(DISTINCT f.psName) = :facilitiesCount
        )
    """)
    List<RestArea> findByBrandsAndFacilities(
            @Param("brands") List<String> brands,
            @Param("brandsCount") long brandsCount,
            @Param("facilities") List<String> facilities,
            @Param("facilitiesCount") long facilitiesCount);

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

    @Query(value = """
        SELECT DISTINCT ra.* FROM rest_area ra
        LEFT JOIN rest_area_brand b ON ra.std_rest_nm = b.std_rest_nm
        LEFT JOIN rest_area_facility f ON ra.std_rest_nm = f.std_rest_nm
        LEFT JOIN rest_area_food food ON ra.std_rest_nm = food.std_rest_nm
        WHERE ra.rest_area_nm LIKE %:keyword%
           OR ra.road_address LIKE %:keyword%
           OR b.brd_name LIKE %:keyword%
           OR f.ps_name LIKE %:keyword%
           OR food.food_nm LIKE %:keyword%
    """, nativeQuery = true)
    List<RestArea> searchByKeyword(@Param("keyword") String keyword);
}
