package com.goomoong.room9backend.Repository;

import com.goomoong.room9backend.domain.room.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Override @EntityGraph(attributePaths = {"users"})
    List<Room> findAll();

    @Query("select r from Room r join fetch r.amenities a join fetch r.roomConfigures c where r.id = :id")
    Room SelectForDelete(@Param("id") Long id);
}
