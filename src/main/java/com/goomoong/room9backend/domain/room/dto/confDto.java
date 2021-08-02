package com.goomoong.room9backend.domain.room.dto;

import com.goomoong.room9backend.domain.room.RoomConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class confDto {

    private String confType;
    private int count;

    public confDto(RoomConfiguration roomConfiguration) {
        this.confType = roomConfiguration.getConfType();
        this.count = roomConfiguration.getCount();
    }
}
