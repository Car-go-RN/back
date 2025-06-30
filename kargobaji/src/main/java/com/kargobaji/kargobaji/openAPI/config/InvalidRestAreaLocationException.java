package com.kargobaji.kargobaji.openAPI.config;

public class InvalidRestAreaLocationException extends RuntimeException {
    public InvalidRestAreaLocationException(String restAreaName) {
        super("휴게소 위치 정보가 누락되었습니다: " + restAreaName);
    }
}
