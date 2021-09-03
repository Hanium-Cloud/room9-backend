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
public class Reply extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_authority_id")
    private CommunityAuthority communityAuthority;

    private String content;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikeReply> likes = new ArrayList<>();

    public void update(String content) {
        this.content = content;
    }

    public int getLikeNum(){
        return getLikes().size();
    }
}
