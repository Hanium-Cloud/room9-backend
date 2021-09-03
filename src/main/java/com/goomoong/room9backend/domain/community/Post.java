package com.goomoong.room9backend.domain.community;

import com.goomoong.room9backend.domain.base.BaseEntity;
import com.goomoong.room9backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_authority_id")
    private CommunityAuthority communityAuthority;

    private String sigCode;

    private String title;

    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikePost> likes = new ArrayList<>();


    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getReplyNum(){
        return getReplies().size();
    }

    public int getLikeNum() {
        return getLikes().size();
    }
}
