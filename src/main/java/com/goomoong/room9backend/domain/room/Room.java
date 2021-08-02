package com.goomoong.room9backend.domain.room;

import com.goomoong.room9backend.domain.room.dto.UpdateRequestRoomDto;
import com.goomoong.room9backend.domain.room.dto.confDto;
import com.goomoong.room9backend.domain.user.User;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
public class Room {

    @Id @GeneratedValue
    @Column(name = "Room_Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    @ElementCollection
    @CollectionTable(name = "Room_Amenity",
            joinColumns = @JoinColumn(name = "Room_Id"))
    @Column(name = "Facility")
    private Set<String> amenities = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "Room_Configuration",
            joinColumns = @JoinColumn(name = "Room_Id"))
    private Set<RoomConfiguration> roomConfigures = new HashSet<>();

    private LocalDateTime createdDate; // 올린 날짜
    private LocalDateTime modifiedDate; // 수정한 날짜
    private int limited; // 제한 인원
    private int price; // 가격
    private String title; // 제목
    private String content; // 설명
    private String detailLocation; // 상세 위치
    private String rule; // 규칙
    private int charge; // 추가인원 요금
    private int liked; // 좋아요

    //== 양방향 연관관계 메서드 ==//
    public void setUsers(User users) {
        this.users = users;
        users.getRooms().add(this);
    }

    public static Room createRoom(User users, List<confDto> confs, int limited, int price,
                                  String title, String content, String detailLocation, String rule,
                                  int charge, List<String> facilities) {
        Room room = new Room();
        room.setUsers(users);
        room.limited = limited;
        room.price = price;
        room.title = title;
        room.content = content;
        room.detailLocation = detailLocation;
        room.createdDate = LocalDateTime.now();
        room.rule = rule;
        room.charge = charge;
        room.liked = 0;

        for (confDto conf : confs) { room.getRoomConfigures()
                .add(new RoomConfiguration(conf.getConfType(), conf.getCount())); }

        for (String facility : facilities) { room.getAmenities().add(facility); }

        return room;
    }

    public void modifyRoom(UpdateRequestRoomDto request) {

        this.limited = request.getLimit();
        this.price = request.getPrice();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.detailLocation = request.getDetailLocation();
        this.modifiedDate = LocalDateTime.now();
        this.rule = request.getRule();
        this.charge = request.getAddCharge();

        this.getRoomConfigures().removeAll(this.getRoomConfigures());
        for (confDto confDto : request.getConf()) {
            this.getRoomConfigures()
                    .add(new RoomConfiguration(confDto.getConfType(), confDto.getCount()));
        }

        this.getAmenities().removeAll(this.getAmenities());
        for (String facility : request.getFacilities()) {
            this.getAmenities().add(facility);
        }
    }
}
