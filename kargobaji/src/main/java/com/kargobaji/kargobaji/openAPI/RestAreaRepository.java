package com.kargobaji.kargobaji.openAPI;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestAreaRepository extends JpaRepository<RestArea, Long> {
}
