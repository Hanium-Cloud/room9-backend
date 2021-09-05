package com.goomoong.room9backend.repository.chat;

import com.goomoong.room9backend.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}