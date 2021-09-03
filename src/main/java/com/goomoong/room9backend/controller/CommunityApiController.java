package com.goomoong.room9backend.controller;

import com.goomoong.room9backend.domain.community.CommunityAuthority;
import com.goomoong.room9backend.domain.community.Post;
import com.goomoong.room9backend.domain.community.Reply;
import com.goomoong.room9backend.domain.community.dto.*;
import com.goomoong.room9backend.domain.user.User;
import com.goomoong.room9backend.service.UserService;
import com.goomoong.room9backend.service.community.CommunityAuthorityService;
import com.goomoong.room9backend.service.community.LikeService;
import com.goomoong.room9backend.service.community.PostService;
import com.goomoong.room9backend.service.community.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommunityApiController {

    private final CommunityAuthorityService caService;
    private final PostService postService;
    private final ReplyService replyService;
    private final UserService userService;
    private final LikeService likeService;

    /***
     * community api
     */

    @GetMapping("/api/v1/communities")
    public SelectCommunityResponseDto selectCommunityV1(@RequestBody SelectCommunityRequestDto request){
        List<CommunityAuthority> findCommunities = caService.findByUserId(request.getUserId());

        List<CommunityDto> collect = findCommunities.stream()
                .map(c -> CommunityDto.builder()
                        .sigCode(c.getSigCode())
                        .checkIn(c.getCheckIn())
                        .checkOut(c.getCheckOut())
                        .peopleNum(caService.peopleNumInCommunity(c.getSigCode(), c.getCheckIn(), c.getCheckOut()))
                        .build()
                )
                .collect(Collectors.toList());


        return new SelectCommunityResponseDto(collect);
    }

    @GetMapping("/api/v1/communities/{sigCode}")
    public SelectPostResponseDto selectPostListV1(@PathVariable String sigCode, @RequestBody SelectPostRequestDto request){

        CommunityAuthority findCa = caService.findByUserIdAndSigCode(request.getUserId(), sigCode);

        List<Post> findPosts = postService.findByReservationTerm(findCa.getSigCode(), findCa.getCheckIn(), findCa.getCheckOut());

        List<PostListDto> collect = findPosts.stream()
                .map(p -> PostListDto.builder()
                        .postId(p.getId())
                        .nickName(p.getUser().getNickname())
                        .checkIn(p.getCommunityAuthority().getCheckIn())
                        .checkOut(p.getCommunityAuthority().getCheckOut())
                        .title(p.getTitle())
                        .replyNum(p.getReplyNum())
                        .likeNum(p.getLikeNum())
                        .createdTime(p.getCreatedDate())
                        .modifiedTime(p.getUpdatedDate())
                        .build()
                )
                .collect(Collectors.toList());


        return new SelectPostResponseDto(collect);
    }


    /***
     * post api
     */

    @GetMapping("/api/v1/posts/{sigCode}/{id}")
    public PostDto selectPostV1(@PathVariable String sigCode, @PathVariable Long id, @RequestBody SelectPostRequestDto request){
        caService.checkPostAuth(request.getUserId(), sigCode);

        Post post = postService.findById(id);
        List<Reply> findReplies = replyService.findByPostId(id);

        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .nickName(post.getUser().getNickname())
                .checkIn(post.getCommunityAuthority().getCheckIn())
                .checkOut(post.getCommunityAuthority().getCheckOut())
                .title(post.getTitle())
                .content(post.getContent())
                .createdTime(post.getCreatedDate())
                .modifiedTime(post.getUpdatedDate())
                .replies(
                        findReplies.stream()
                                .map(r -> ReplyDto.builder()
                                        .id(r.getId())
                                        .nickname(r.getUser().getNickname())
                                        .checkIn(r.getCommunityAuthority().getCheckIn())
                                        .checkOut(r.getCommunityAuthority().getCheckOut())
                                        .content(r.getContent())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();

        return postDto;
    }


    @PostMapping("/api/v1/posts/{sigCode}")
    public CreatePostResponseDto savePostV1(@PathVariable String sigCode, @RequestBody CreatePostRequestDto request){
        CommunityAuthority findCa = caService.findByUserIdAndSigCode(request.getUserId(), sigCode);

        User user = userService.findById(request.getUserId());

        Post post = Post.builder()
                .user(user)
                .communityAuthority(findCa)
                .sigCode(sigCode)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Post savedPost = postService.save(post);

        return new CreatePostResponseDto(savedPost.getId());
    }

    @PatchMapping("/api/v1/posts/{id}")
    public UpdatePostResponseDto updatePostV1(@PathVariable Long id, @RequestBody UpdatePostRequestDto request){
        Post post = postService.update(id, request.getTitle(), request.getContent());
        return new UpdatePostResponseDto(post.getId());
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public void deletePostV1(@PathVariable Long id){
        postService.delete(id);
    }

    // 게시글 좋아요
    @GetMapping("/api/v1/posts/{id}/like")
    public IsLikedDto checkPostLikeV1(@PathVariable Long id, @RequestBody Long userId){
        boolean findLike = likeService.checkPostLike(userId, id);

        if (findLike) {
            int likeNum = likeService.likeNumPost(id);
            return new IsLikedDto(true, likeNum);
        }
        else{
            return new IsLikedDto(false, 0);
        }

    }

    @PostMapping("/api/v1/posts/{id}/like")
    public void changePostLikeV1(@PathVariable Long id, @RequestBody Long userId){
        likeService.changePostLike(userId, id);
    }



    /**
     * reply api
     */

    @PostMapping("/api/v1/{postId}/{sigCode}/replies")
    public CreateReplyResponseDto saveReplyV1(@PathVariable Long postId, @PathVariable String sigCode, @RequestBody CreateReplyRequestDto request){

        CommunityAuthority findCa = caService.findByUserIdAndSigCode(request.getUserId(), sigCode);

        User user = userService.findById(request.getUserId());
        Post post = postService.findById(postId);

        Reply reply = Reply.builder()
                .post(post)
                .user(user)
                .communityAuthority(findCa)
                .content(request.getContent())
                .build();

        Reply savedReply = replyService.save(reply);

        return new CreateReplyResponseDto(savedReply.getId());
    }

    @PatchMapping("/api/v1/replies/{id}")
    public UpdateReplyResponseDto updateReplyV1(@PathVariable Long id, @RequestBody UpdateReplyRequestDto request){

        replyService.update(id, request.getReplyContent());
        Reply findReply = replyService.findById(id);

        return new UpdateReplyResponseDto(findReply.getId());
    }

    @DeleteMapping("/api/v1/replies/{id}")
    public void deleteReplyV1(@PathVariable Long id){
        replyService.delete(id);
    }

    // 댓글 좋아요
    @GetMapping("/api/v1/replies/{id}/like")
    public IsLikedDto checkReplyLikeV1(@PathVariable Long id, @RequestBody Long userId){
        boolean findLike = likeService.checkReplyLike(userId, id);

        if(findLike){
            int likeNum = likeService.likeNumReply(id);
            return new IsLikedDto(true, likeNum);
        }
        else{
            return new IsLikedDto(false, 0);
        }
    }

    @PostMapping("/api/v1/replies/{id}/like")
    public void changeReplyLikeV1(@PathVariable Long id, @RequestBody Long userId){
        likeService.changeReplyLike(userId, id);
    }

}
