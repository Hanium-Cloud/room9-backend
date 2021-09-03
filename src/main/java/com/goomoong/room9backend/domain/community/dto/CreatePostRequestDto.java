package com.goomoong.room9backend.domain.community.dto;

import lombok.Data;

@Data
public class CreatePostRequestDto {

    private Long userId;
    private String title;
    private String content;
}
