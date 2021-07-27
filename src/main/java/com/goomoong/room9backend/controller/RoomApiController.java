package com.goomoong.room9backend.controller;

import com.goomoong.room9backend.Repository.RoomRepository;
import com.goomoong.room9backend.Service.RoomService;
import com.goomoong.room9backend.domain.room.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoomApiController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    @PostMapping("/room/create")
    public CreatedResponseRoomDto createdRoom(@RequestBody CreatedRequestRoomDto request) {

        Long roomId = roomService.addRoom(request);

        return new CreatedResponseRoomDto(roomId);
    }

    @PutMapping("/room/update/{roomId}")
    public void updateRoom(@PathVariable("roomId") Long id, @RequestBody UpdateRequestRoomDto request) {
        roomService.updateRoom(id, request);
    }

    @GetMapping("/room")
    public roomData getAllRoom(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<GetResponseRoomDto> rooms = roomRepository.findAll(offset, limit)
                        .stream().map(r -> new GetResponseRoomDto(r)).collect(Collectors.toList());

        return new roomData(rooms.size(), rooms);
    }


}
