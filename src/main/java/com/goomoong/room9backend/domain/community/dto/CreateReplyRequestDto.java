package com.goomoong.room9backend.domain.community.dto;

import lombok.Data;

@Data
public class CreateReplyRequestDto {

    private Long userId;
    private String content;
}
