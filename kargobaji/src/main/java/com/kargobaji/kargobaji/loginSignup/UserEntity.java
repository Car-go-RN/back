//package com.kargobaji.kargobaji.loginSignup;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//
//public class UserEntity {
//    @Entity
//    @Builder
//    @NoArgsConstructor(access = PROTECTED)
//    @AllArgsConstructor(access = PRIVATE)
//    public class User {
//        @Id @GeneratedValue
//        @Column(name = "user_id")
//        private Long id;
//
//        @Column(length = 25)
//        private String email;
//
//        @Column(length = 20, nullable = false)
//        private String password;
//
//        @Column(length = 20, nullable = false)
//        private String nickname;
//    }
//}
