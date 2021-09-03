package com.goomoong.room9backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReplyDto {

    private Long id;
    private String nickname;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String content;
}
