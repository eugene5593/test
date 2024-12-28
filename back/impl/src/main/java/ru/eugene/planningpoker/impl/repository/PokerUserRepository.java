package ru.eugene.planningpoker.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eugene.planningpoker.api.entity.PokerUser;
import ru.eugene.planningpoker.api.entity.Room;

import java.util.List;
import java.util.Optional;

public interface PokerUserRepository extends JpaRepository<PokerUser, Long> {
    List<PokerUser> findByRoom(Room room);
    Optional<PokerUser> findBySessionId(String sessionId);
    Optional<PokerUser> findByUserId(String userId);
    List<PokerUser> findByRoomOrderByJoinedAtAsc(Room room);
}
