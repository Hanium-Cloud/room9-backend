package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByPostId(Long id);
}
