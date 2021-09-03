package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom{
}
