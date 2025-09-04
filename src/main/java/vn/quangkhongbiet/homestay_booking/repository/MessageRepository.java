package vn.quangkhongbiet.homestay_booking.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByConversation(Conversation conversation, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.timestamp < :cursor ORDER BY m.timestamp DESC")
    List<Message> findMessages(@Param("conversationId") Long conversationId, @Param("cursor") Instant cursor, Pageable pageable);
}
