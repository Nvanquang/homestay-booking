package vn.quangkhongbiet.homestay_booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.quangkhongbiet.homestay_booking.domain.chat.entity.Conversation;
import vn.quangkhongbiet.homestay_booking.domain.user.entity.User;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    boolean existsById(Long id);

    Conversation findByUserAndHostAndHomestayId(User user, User host, Long homestayId);

    @Query(value = "SELECT * FROM conversations c " +
               "WHERE c.user_id = :userId OR c.host_id = :userId " +
               "ORDER BY c.last_activity DESC",
       nativeQuery = true)
    List<Conversation> findByUserOrHost(@Param("userId") Long userId);
}
