package com.goomoong.room9backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityDto {

    private String sigCode;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int peopleNum;
}
