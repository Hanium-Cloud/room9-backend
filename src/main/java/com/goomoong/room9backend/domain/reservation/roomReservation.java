package com.goomoong.room9backend.domain.reservation;

import com.goomoong.room9backend.domain.base.BaseEntity;
import com.goomoong.room9backend.domain.reservation.dto.ReservationDto;
import com.goomoong.room9backend.domain.room.Room;
import com.goomoong.room9backend.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class roomReservation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation_Id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room_Id")
    private Room room;

    //예약자 == 현재 로그인해서 예약버튼 누른 사람
    //ROLE.GUEST일 경우에만 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserveStatus")
    private ReserveStatus reserveStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "refundStatus")
    private RefundStatus refundStatus;
    private LocalDateTime startDate; // DatePicker
    private LocalDateTime finalDate;
}
