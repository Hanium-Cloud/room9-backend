package com.goomoong.room9backend.domain.community.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostListDto {

    private Long postId;
    private String nickName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String title;
    private int likeNum;
    private int replyNum;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}
