package com.goomoong.room9backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectPostResponseDto<T> {
    private T data;
}