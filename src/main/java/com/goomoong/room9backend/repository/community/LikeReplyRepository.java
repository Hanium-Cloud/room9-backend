package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.LikeReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeReplyRepository extends JpaRepository<LikeReply, Long> {

    Optional<LikeReply> findByUserIdAndReplyId(Long userId, Long replyId);
}
