package com.kargobaji.kargobaji.openAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rest_area_local")
public class RestAreaLocal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stdRestNm;

    private String latitude; // 위도
    private String longitude; // 경도

    private String local; // 도로명

}
