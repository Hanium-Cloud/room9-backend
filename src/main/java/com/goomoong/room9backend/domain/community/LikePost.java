package com.goomoong.room9backend.domain.community;

import com.goomoong.room9backend.domain.user.User;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Builder
@NoArgsConstructor
public class LikePost extends Like{

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
