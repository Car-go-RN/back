package com.kargobaji.kargobaji.favorite.dto;

import com.kargobaji.kargobaji.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long user;
    private Long restArea;
    private String restAreaNm;
    private String message;

    public static FavoriteResponse fromEntity(Favorite favorite, String message){
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getUser().getId(),
                favorite.getRestArea().getId(),
                favorite.getRestArea().getRestAreaNm(),
                message
        );
    }
}
