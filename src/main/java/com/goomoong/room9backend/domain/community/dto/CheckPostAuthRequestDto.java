package com.goomoong.room9backend.domain.community.dto;

import lombok.Data;

@Data
public class CheckPostAuthRequestDto {

    private Long userId;
    private String sigCode;
}
