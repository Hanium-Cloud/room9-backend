package com.goomoong.room9backend.domain.community.dto;

import lombok.Data;

@Data
public class UpdatePostRequestDto {

    private Long userId;
    private String title;
    private String content;
}
