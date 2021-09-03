package com.goomoong.room9backend.service.community;

import com.goomoong.room9backend.domain.community.LikePost;
import com.goomoong.room9backend.domain.community.LikeReply;
import com.goomoong.room9backend.domain.community.Post;
import com.goomoong.room9backend.domain.community.Reply;
import com.goomoong.room9backend.domain.user.User;
import com.goomoong.room9backend.repository.community.LikePostRepository;
import com.goomoong.room9backend.repository.community.LikeReplyRepository;
import com.goomoong.room9backend.repository.community.PostRepository;
import com.goomoong.room9backend.repository.community.ReplyRepository;
import com.goomoong.room9backend.repository.user.UserRepository;
import com.goomoong.room9backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final LikeReplyRepository likeReplyRepository;
    private final UserService userService;
    private final PostService postService;
    private final ReplyService replyService;
    
    //post 좋아요
    public boolean checkPostLike(Long userId, Long postId){
        Optional<LikePost> findLike = likePostRepository.findByUserIdAndPostId(userId, postId);

        if(findLike.isPresent()){
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional
    public void changePostLike(Long userId, Long postId){
        Optional<LikePost> findLike = likePostRepository.findByUserIdAndPostId(userId, postId);

        if(findLike.isPresent()){
            likePostRepository.delete(findLike.get());
        }
        else{
            User user = userService.findById(userId);
            Post post = postService.findById(postId);

            LikePost likePost = LikePost.builder().user(user).post(post).build();

            likePostRepository.save(likePost);
        }
    }

    public int likeNumPost(Long id){
        Post post = postService.findById(id);
        return post.getLikeNum();
    }

    
    //reply 좋아요
    public boolean checkReplyLike(Long userId, Long replyId){
        Optional<LikeReply> findLike = likeReplyRepository.findByUserIdAndReplyId(userId, replyId);

        if(findLike.isPresent()){
            return true;
        }
        else{
            return false;
        }
    }

    @Transactional
    public void changeReplyLike(Long userId, Long replyId){
        Optional<LikeReply> findLike = likeReplyRepository.findByUserIdAndReplyId(userId, replyId);

        if(findLike.isPresent()){
            likeReplyRepository.delete(findLike.get());
        }
        else{
            User user = userService.findById(userId);
            Reply reply = replyService.findById(replyId);

            LikeReply likeReply = LikeReply.builder().user(user).reply(reply).build();

            likeReplyRepository.save(likeReply);
        }
    }

    public int likeNumReply(Long id){
        Reply reply = replyService.findById(id);
        return reply.getLikeNum();
    }
}
