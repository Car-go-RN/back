    package com.kargobaji.kargobaji.openAPI.entity;

    import com.kargobaji.kargobaji.favorite.entity.Favorite;
    import com.kargobaji.kargobaji.like.entity.Like;
    import com.kargobaji.kargobaji.review.entity.Review;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.ColumnDefault;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Table(name = "rest_area")
    public class RestArea {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String stdRestNm; // 주유소 휴게소 이름
        private String gasolinePrice; //휘발유
        private String diselPrice; // 경유
        private String lpgPrice; // LPG

        private String electric;// 전기 충전소 여부
        private String hydrogen; // 수소 충전소 여부

        private String roadAddress;
        private String phone;
        private Double latitude; // 위도 (y)
        private Double longitude; // 경도 (x)

        private String restAreaNm; // api에서 가져온 휴게소 이름(대표 이름)

        @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Review> reviewList = new ArrayList<>();

        @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Like> likeList = new ArrayList<>();

        @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Favorite> favoriteList = new ArrayList<>();
    }
