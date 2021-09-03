package com.goomoong.room9backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IsLikedDto {
    private boolean isLiked;
    private int likeNum;
}
