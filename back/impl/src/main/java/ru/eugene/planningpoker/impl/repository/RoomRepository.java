package ru.eugene.planningpoker.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.eugene.planningpoker.api.entity.Room;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomId(String roomId);

    @Query("SELECT r FROM Room r LEFT JOIN FETCH r.users WHERE r.roomId = :roomId")
    Optional<Room> findByRoomIdWithUsers(@Param("roomId") String roomId);
}
