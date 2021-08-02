package com.goomoong.room9backend.domain.room.dto;

import com.goomoong.room9backend.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetResponseRoomDto {

    private Long roomId;
    private String username; // 판매자

    private String title;
    private String content;
    private String rule; //방 규칙

    private String Location;

    private int price;
    private int excessCharge; // 초과인원 요금
    private int limitPeople;
    private int like; // 좋아요

    private LocalDateTime createTime;
    private LocalDateTime modifiedTime;

    private Set<confDto> room_configuration; // 구성
    private Set<amenityDto> room_amenities; // 부대시설

    public GetResponseRoomDto(Room room) {
        this.roomId = room.getId();
        this.username = room.getUsers().getNickname();
        this.title = room.getTitle();
        this.content = room.getContent();
        this.rule = room.getRule();
        this.Location = room.getDetailLocation();
        this.price = room.getPrice();
        this.excessCharge = room.getCharge();
        this.limitPeople = room.getLimited();
        this.like = room.getLiked();
        this.createTime = room.getCreatedDate();
        this.modifiedTime = room.getModifiedDate();
        this.room_amenities = room.getAmenities()
                .stream().map(a -> new amenityDto(a)).collect(Collectors.toSet());
        this.room_configuration = room.getRoomConfigures()
                .stream().map(c -> new confDto(c)).collect(Collectors.toSet());
    }
}
