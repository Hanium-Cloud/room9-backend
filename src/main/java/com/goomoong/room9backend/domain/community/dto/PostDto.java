package com.goomoong.room9backend.domain.community.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDto {

    private Long id;
    private String nickName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private List<ReplyDto> replies;

}
