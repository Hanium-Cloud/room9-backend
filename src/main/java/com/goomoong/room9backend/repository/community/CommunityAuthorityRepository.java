package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.CommunityAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommunityAuthorityRepository extends JpaRepository<CommunityAuthority, Long> {

    List<CommunityAuthority> findByUserId(Long id);
    List<CommunityAuthority> findByUserIdAndSigCodeOrderByCheckInDesc(Long id, String sigCode);
    List<CommunityAuthority> findBySigCodeAndCheckInBeforeOrCheckOutAfter(String sigCode, LocalDateTime checkOut, LocalDateTime checkIn);
}
