package com.goomoong.room9backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectCommunityResponseDto<T> {

    private T data;
}
