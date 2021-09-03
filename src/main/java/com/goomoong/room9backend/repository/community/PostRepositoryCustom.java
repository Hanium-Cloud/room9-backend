package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findByReservationTerm(String sigCode, LocalDateTime checkIn, LocalDateTime checkOut);
}
