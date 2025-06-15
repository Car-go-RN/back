package com.kargobaji.kargobaji.like.dto;

import com.kargobaji.kargobaji.like.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeResponse {
    private Long id;
    private Long user;
    private Long restArea;
    private String restAreaNm;
    private String message;

    public static LikeResponse fromEntity(Like like, String message){
        return new LikeResponse(
                like.getId(),
                like.getUser().getId(),
                like.getRestArea().getId(),
                like.getRestArea().getRestAreaNm(),
                message
        );
    }
}
