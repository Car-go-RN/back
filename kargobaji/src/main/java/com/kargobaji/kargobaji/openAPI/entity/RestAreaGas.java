package com.kargobaji.kargobaji.openAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rest_area_gas")
public class RestAreaGas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stdRestNm;
    private String gasolinePrice; //휘발유
    private String diselPrice; // 경유
    private String lpgPrice; // LPG
}
