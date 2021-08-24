package com.goomoong.room9backend.domain.review;

import com.goomoong.room9backend.domain.base.BaseEntity;
import com.goomoong.room9backend.domain.room.Room;
import com.goomoong.room9backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor
@Table(name = "reviews")
@Entity
public class Review extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room_Id")
    private Room room;

    private String reviewContent;

    private int reviewScore;

    @Builder
    public Review(Long id, User user, Room room, String reviewContent, int reviewScore){
        this.id = id;
        this.user = user;
        this.room = room;
        this.reviewContent = reviewContent;
        this.reviewScore = reviewScore;
    }

    public void update(String reviewContent, int reviewScore) {
        this.reviewContent = reviewContent;
        this.reviewScore = reviewScore;
    }

}
