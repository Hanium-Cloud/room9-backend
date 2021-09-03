package com.goomoong.room9backend.repository.community;

import com.goomoong.room9backend.domain.community.Post;
import com.goomoong.room9backend.domain.community.QCommunityAuthority;
import com.goomoong.room9backend.domain.reservation.QroomReservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.goomoong.room9backend.domain.community.QPost.post;
import static com.goomoong.room9backend.domain.community.QCommunityAuthority.communityAuthority;
import static com.goomoong.room9backend.domain.reservation.QroomReservation.roomReservation;
import static com.goomoong.room9backend.domain.user.QUser.user;



@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findByReservationTerm(String sigCode, LocalDateTime checkIn, LocalDateTime checkOut){

        return queryFactory
                .select(post)
                .from(post)
                .where(post.user.in(
                        queryFactory
                                .select(communityAuthority.user)
                                .from(communityAuthority)
                                .where(
                                        communityAuthority.checkIn.before(checkOut).or(communityAuthority.checkOut.after(checkIn))
                                )
                                .fetch()
                ))
                .orderBy(post.createdDate.desc())
                .fetch();
    }
}