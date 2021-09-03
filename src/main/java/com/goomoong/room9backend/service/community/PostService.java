package com.goomoong.room9backend.service.community;

import com.goomoong.room9backend.domain.community.Post;
import com.goomoong.room9backend.exception.NoSuchPostException;
import com.goomoong.room9backend.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Post save(Post post){
        postRepository.save(post);
        return post;
    }

    @Transactional
    public Post update(Long id, String title, String content){
        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchPostException("존재하지 않는 게시글입니다."));
        post.update(title, content);
        return post;
    }

    @Transactional
    public void delete(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchPostException("존재하지 않는 게시글입니다."));

        postRepository.delete(post);
    }

    public Post findById(Long id){
        return postRepository.findById(id).orElseThrow(() -> new NoSuchPostException("존재하지 않는 게시글입니다."));
    }

    public List<Post> findByReservationTerm(String sigCode, LocalDateTime checkIn, LocalDateTime checkOut){
        return postRepository.findByReservationTerm(sigCode, checkIn, checkOut);
    }

}
