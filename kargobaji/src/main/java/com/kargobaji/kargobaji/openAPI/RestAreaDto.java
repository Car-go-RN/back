package com.kargobaji.kargobaji.openAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestAreaDto {
    private String brdName;
    private String stdRestNm;
    private String stime;
    private String etime;
}
