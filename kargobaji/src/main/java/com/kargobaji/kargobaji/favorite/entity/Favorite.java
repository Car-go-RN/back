package com.kargobaji.kargobaji.favorite.entity;

import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.openAPI.entity.RestArea;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restArea_id")
    private RestArea restArea;

    @Builder
    public Favorite(User user, RestArea restArea) {
        this.user = user;
        this.restArea = restArea;
    }
}
