package com.goomoong.room9backend.repository.user;

import com.goomoong.room9backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccountId(String accountId);

    Optional<User> findByNickname(String nickname);
}
