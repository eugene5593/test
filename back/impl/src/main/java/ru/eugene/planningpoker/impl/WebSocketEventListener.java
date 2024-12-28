package ru.eugene.planningpoker.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.eugene.planningpoker.api.DTO.PokerUserDTO;
import ru.eugene.planningpoker.api.entity.PokerUser;
import ru.eugene.planningpoker.api.entity.Room;
import ru.eugene.planningpoker.impl.converter.ConverterPokerUsers;
import ru.eugene.planningpoker.impl.service.PokerUserService;
import ru.eugene.planningpoker.impl.service.RoomService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class WebSocketEventListener {

    @Autowired
    private PokerUserService pokerUserService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ConverterPokerUsers converterPokerUsers;

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final String sessionId = event.getSessionId();
        System.out.println("Attempting to handle session disconnect for session: " + sessionId);

        final Optional<PokerUser> optionalUser = pokerUserService.findBySessionId(sessionId);
        if (optionalUser.isPresent()) {
            final PokerUser user = optionalUser.get();
            final Room room = user.getRoom();
            final boolean wasLeader = user.isLeader();

            System.out.println("User found for session disconnect: " + user.getName());

            // Удаляем пользователя из базы данных в отдельной транзакции
            pokerUserService.deleteUser(user);

            // Выполняем проверку на пустую комнату в отдельной транзакции
            roomService.deactivateRoomIfEmpty(room);

            // Получаем оставшихся пользователей в комнате
            final List<PokerUser> usersInRoom = pokerUserService.findUsersByRoom(room);

            // Если пользователь был лидером и есть другие пользователи, назначаем нового лидера
            if (wasLeader && !usersInRoom.isEmpty()) {
                final PokerUser newLeader = usersInRoom.stream()
                                                       .min(Comparator.comparing(PokerUser::getJoinedAt))
                                                       .orElse(usersInRoom.getFirst());
                newLeader.setLeader(true);
                pokerUserService.saveUser(newLeader);
                System.out.println("New leader assigned: " + newLeader.getName());
            }

            // Отправляем обновленный список пользователей всем в комнате
            if (!usersInRoom.isEmpty()) {
                sendUpdatedUserList(room);
            }
        } else {
            System.out.println("No user found for session disconnect: " + sessionId);
        }
    }


    public void sendUpdatedUserList(final Room room) {
        final List<PokerUser> users = pokerUserService.findUsersByRoom(room);

        final List<PokerUserDTO> userDTOs = users.stream()
                                                 .map(converterPokerUsers::convertPokerUserToPokerUserDTO)
                                                 .toList();

        double averageResult = 0;
        final List<Double> selectedNumbers = users.stream()
                                                  .map(PokerUser::getSelectedNumber)
                                                  .filter(Objects::nonNull)
                                                  .map(numberStr -> {
                                                      try {
                                                          return Double.parseDouble(numberStr);
                                                      } catch (final NumberFormatException e) {
                                                          return null;
                                                      }
                                                  })
                                                  .filter(Objects::nonNull)
                                                  .toList();

        if (!selectedNumbers.isEmpty()) {
            final double sum = selectedNumbers.stream().mapToDouble(Double::doubleValue).sum();
            averageResult = sum / selectedNumbers.size();
        }

        final Map<String, Object> payload = Map.of(
                "users", userDTOs,
                "votingComplete", room.isVotingComplete(),
                "averageResult", averageResult
        );

        messagingTemplate.convertAndSend("/topic/room/" + room.getRoomId(), payload);
        System.out.println("Updated user list sent to room: " + room.getRoomId());
    }
}
