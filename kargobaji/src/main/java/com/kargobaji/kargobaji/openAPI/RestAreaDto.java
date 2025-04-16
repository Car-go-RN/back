package com.kargobaji.kargobaji.openAPI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestAreaDto {
    private String brdName;
    private LocalDateTime stime;
    private LocalDateTime etime;
    private String stdRestNm;
}
