package ru.eugene.planningpoker.impl.service;

import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.eugene.planningpoker.api.entity.PokerUser;
import ru.eugene.planningpoker.api.entity.Room;
import ru.eugene.planningpoker.impl.repository.PokerUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PokerUserService {

    @Autowired
    private PokerUserRepository pokerUserRepository;

    @Autowired
    private RoomService roomService;

    @Transactional
    public PokerUser createUser(final String name, final Room room, final String sessionId, final String userId, final boolean leader) {
        final PokerUser user = new PokerUser();
        user.setName(name);
        user.setRoom(room);
        user.setSessionId(sessionId);
        user.setUserId(userId);
        user.setLeader(leader);

        // Установка времени входа
        user.setJoinedAt(LocalDateTime.now());

        return pokerUserRepository.save(user);
    }

    @Transactional
    public void saveUser(final PokerUser user) {
        final PokerUser savedUser = pokerUserRepository.save(user);
        System.out.println("Пользователь обновлён в базе данных: ID " + savedUser.getId());
    }

    @Transactional
    public List<PokerUser> findUsersByRoom(final Room room) {
        return pokerUserRepository.findByRoomOrderByJoinedAtAsc(room);
    }

    public Optional<PokerUser> findBySessionId(final String sessionId) {
        return pokerUserRepository.findBySessionId(sessionId);
    }

    public Optional<PokerUser> findByUserId(final String userId) {
        return pokerUserRepository.findByUserId(userId);
    }

    @Transactional
    public void updateSelectedNumber(final PokerUser user, final String selectedNumber) {
        user.setSelectedNumber(selectedNumber);
        pokerUserRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(final PokerUser user) {
        try {
            System.out.println("Attempting to delete user: " + user.getName() + " from room: " + user.getRoom().getRoomId());
            pokerUserRepository.delete(user);
        } catch (final ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
            System.out.println("User already deleted or updated by another transaction: " + user.getName());
        }
    }


}
