package com.goomoong.room9backend.service.room;

import com.goomoong.room9backend.domain.reservation.dto.ReservationDto;
import com.goomoong.room9backend.domain.reservation.roomReservation;
import com.goomoong.room9backend.domain.review.dto.scoreDto;
import com.goomoong.room9backend.domain.room.Amenity;
import com.goomoong.room9backend.domain.room.RoomConfiguration;
import com.goomoong.room9backend.domain.room.RoomLike;
import com.goomoong.room9backend.domain.room.dto.*;
import com.goomoong.room9backend.domain.user.Role;
import com.goomoong.room9backend.exception.RoomNotAddException;
import com.goomoong.room9backend.repository.reservation.roomReservationRepository;
import com.goomoong.room9backend.repository.room.RoomLikeRepository;
import com.goomoong.room9backend.service.ReviewService;
import com.goomoong.room9backend.service.UserService;
import com.goomoong.room9backend.service.file.FileService;
import com.goomoong.room9backend.config.FolderConfig;
import com.goomoong.room9backend.repository.room.RoomImgRepository;
import com.goomoong.room9backend.repository.room.RoomRepository;
import com.goomoong.room9backend.domain.file.File;
import com.goomoong.room9backend.domain.file.RoomImg;
import com.goomoong.room9backend.domain.room.Room;
import com.goomoong.room9backend.domain.user.User;
import com.goomoong.room9backend.exception.NoSuchRoomException;
import com.goomoong.room9backend.util.AboutDate;
import com.goomoong.room9backend.util.AboutScore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.goomoong.room9backend.util.AboutDate.compareDay;
import static com.goomoong.room9backend.util.AboutDate.getLocalDateTimeFromString;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final FileService fileService;
    private final RoomImgRepository roomImgRepository;
    private final RoomRepository roomRepository;
    private final FolderConfig folderConfig;
    private final ReviewService reviewService;
    private final RoomLikeRepository roomLikeRepository;
    private final roomReservationRepository roomReservationRepository;

    //??? ??????
    @Transactional
    public Long addRoom(CreatedRequestRoomDto request, User user) throws IOException {

        Set<RoomConfiguration> roomConfig = RoomConfiguration.createRoomConfig(request.getConf());
        Set<Amenity> amenities = Amenity.createRoomFacility(request.getFacilities());

        if(user.getRole() != Role.HOST) {
            throw new RoomNotAddException();
        }

        List<File> files = fileService.uploadFiles(folderConfig.getRoom(), request.getImages());
        Room room = Room.createRoom(user, request, roomConfig, amenities);
        List<RoomImg> roomImgs = new ArrayList<>();

        for (File file : files) {
            roomImgs.add(RoomImg.create(file, room));
        }
        roomRepository.save(room);
        roomImgRepository.saveAll(roomImgs);

        return room.getId();
    }

    public GetDetailRoom getRoomDetail(Long id, User user) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new NoSuchRoomException("???????????? ?????? ????????????."));
        scoreDto scoreDto = reviewService.getAvgScoreAndCount(id);
        Boolean LikeExists = roomLikeRepository.LikeExists(id, user.getId());
        List<roomReservation> reserveList = roomReservationRepository.getAllList(id);

        return new GetDetailRoom(room, scoreDto, LikeExists, reserveList);
    }

    public List<GetCommonRoom> findAll() {

        List<Room> rooms = roomRepository.findAll();
        List<GetCommonRoom> roomList = new ArrayList<>();
        for (Room room : rooms) {
            scoreDto scoredto = reviewService.getAvgScoreAndCount(room.getId());
            roomList.add(new GetCommonRoom(room, scoredto));
        }
        return roomList;
    }

    public roomData.price getTotalPrice(Long id, priceDto priceDto) {

        Room room = roomRepository.findById(id).orElseThrow(() -> new NoSuchRoomException("???????????? ?????? ????????????."));
        long days = compareDay(priceDto.getStartDate(), priceDto.getFinalDate());

        if(priceDto.getPersonnel() == null) {
            return roomData.price.builder()
                    .totalPrice(room.getPrice() * days)
                    .originalPrice(room.getPrice() * days)
                    .charge(Long.valueOf(0))
                    .build();
        }

        if(room.getLimited() < priceDto.getPersonnel()) { // ???????????? ????????????
            long addCharge = room.getCharge() * (priceDto.getPersonnel() - room.getLimited()) * days;
            return roomData.price.builder()
                    .totalPrice(addCharge + room.getPrice() * days)
                    .originalPrice(room.getPrice() * days)
                    .charge(addCharge)
                    .build();
        }
        return roomData.price.builder()
                .totalPrice(room.getPrice() * days)
                .originalPrice(room.getPrice() * days)
                .charge(Long.valueOf(0))
                .build();
    }

    public List<GetCommonRoom> getMyRoom(User user) {

        List<Room> myRooms = roomRepository.findMyRoom(user);
        List<GetCommonRoom> roomList = new ArrayList<>();
        for (Room myRoom : myRooms) {
            scoreDto scoredto = reviewService.getAvgScoreAndCount(myRoom.getId());
            roomList.add(new GetCommonRoom(myRoom, scoredto));
        }
        return roomList;
    }

    @Transactional
    public roomData.liked AboutGoodToRoom(Long id, User user) {
        RoomLike currentRoomLike;
        Room room = roomRepository.findById(id).orElseThrow(() -> new NoSuchRoomException("???????????? ?????? ????????????."));
        Optional<RoomLike> prevGood = Optional.ofNullable(roomLikeRepository.findByRoomIdAndUserId(id, user.getId()));

        if(prevGood.isEmpty()) {
            currentRoomLike = RoomLike.builder().room(room).user(user).likeStatus(true).build();
            room.withGood(currentRoomLike.getLikeStatus());
            roomLikeRepository.save(currentRoomLike);
        } else {
            currentRoomLike = prevGood.get();
            currentRoomLike.setLikeStatus(currentRoomLike.getLikeStatus());
            room.withGood(currentRoomLike.getLikeStatus());
        }

        return new roomData.liked(room.getLiked(), currentRoomLike.getLikeStatus());
    }

    public List<GetCommonRoom> getRoomWithGood(User user) {
        List<RoomLike> roomsWithGood = roomLikeRepository.findRoomWithGood(user);
        List<GetCommonRoom> roomList = new ArrayList<>();
        for (RoomLike roomWithGood : roomsWithGood) {
            scoreDto scoredto = reviewService.getAvgScoreAndCount(roomWithGood.getId());
            roomList.add(new GetCommonRoom(roomWithGood.getRoom(), scoredto));
        }
        return roomList;
    }
}
