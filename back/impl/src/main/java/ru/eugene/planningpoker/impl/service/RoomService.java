package ru.eugene.planningpoker.impl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.eugene.planningpoker.api.entity.Room;
import ru.eugene.planningpoker.api.entity.PokerUser;
import ru.eugene.planningpoker.impl.repository.RoomRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public synchronized Room createRoom(final String roomId) {
        if (roomId != null && !roomId.isBlank()) {
            final Optional<Room> existingRoom = roomRepository.findByRoomId(roomId);
            if (existingRoom.isPresent()) {
                final Room room = existingRoom.get();
                room.setActive(true);
                return roomRepository.save(room);
            }
        }
        final Room room = new Room();
        room.setRoomId(roomId == null || roomId.isBlank() ? String.valueOf(UUID.randomUUID()) : roomId);
        room.setVotingComplete(false);
        room.setActive(true);
        return roomRepository.save(room);
    }

    public Optional<Room> findByRoomId(final String roomId) {
        return roomRepository.findByRoomIdWithUsers(roomId);
    }

    @Transactional
    public void saveRoom(final Room room) {
        roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(final Room room) {
        System.out.println("Deleting room: " + room.getRoomId());
        roomRepository.delete(room);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateRoomIfEmpty(final Room room) {
        System.out.println("deactivateRoomIfEmpty " + room.getRoomId());
        if (room.getUsers().isEmpty()) {
            System.out.println("room.getUsers().isEmpty() " + room.getUsers());
            room.setActive(false);
            room.setVotingComplete(false);
            roomRepository.save(room);
            System.out.println("roomRepository.save(room); " + room);
        } else {
            System.out.println("room.getUsers() not empty " + room.getUsers());
        }
    }

    @Transactional
    public void restoreRoomAndLeader(final Room room) {
        // Восстанавливаем комнату, если она была неактивной
        if (!room.isActive()) {
            room.setActive(true);
        }

        // Проверяем, есть ли в комнате активные пользователи
        final boolean hasLeader = room.getUsers().stream().anyMatch(PokerUser::isLeader);
        if (!hasLeader && !room.getUsers().isEmpty()) {
            // Назначаем нового лидера, если его нет среди пользователей
            room.getUsers().getFirst().setLeader(true);
        }

        // Сохраняем изменения в комнате
        roomRepository.save(room);
    }
}