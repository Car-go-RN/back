package com.kargobaji.kargobaji.loginSignup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    public String getUsername() {
        return this.username;
    }

    public CharSequence getPassword() {
        return this.password;
    }

    public void setPassword(String encode) {
        this.password = encode;
    }
}
