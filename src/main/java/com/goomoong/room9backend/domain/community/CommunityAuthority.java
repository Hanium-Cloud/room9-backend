package com.goomoong.room9backend.domain.community;

import com.goomoong.room9backend.domain.reservation.roomReservation;
import com.goomoong.room9backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
public class CommunityAuthority{

    @Id @GeneratedValue
    @Column(name = "community_authority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomReservation_id")
    private roomReservation Reservation;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reply> replies = new ArrayList<>();

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private String sigCode;
}
