package com.goomoong.room9backend.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String name;

    //TODO: 이미지 테이블 조인
    @Column(nullable = false)
    private String thumbnailUrl;

    @Column
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String intro;

    @Builder
    public User(String accountId, Role role, String name, String thumbnailUrl, String intro) {
        this.accountId = accountId;
        this.role = role;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.intro = intro;
    }

    public void update(String thumbnailUrl, String intro) {
        this.thumbnailUrl = thumbnailUrl;
        this.intro = intro;
    }

    public void changeRole() {
        this.role = (this.role == Role.GUEST) ? Role.HOST : Role.GUEST;
    }

    public static User toEntity(String accountId, Role role, String name, String thumbnailUrl) {
        return User.builder()
                .accountId(accountId)
                .role(role)
                .name(name)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
