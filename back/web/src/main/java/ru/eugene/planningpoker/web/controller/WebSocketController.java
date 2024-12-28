package ru.eugene.planningpoker.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import ru.eugene.planningpoker.api.entity.PokerUser;
import ru.eugene.planningpoker.api.entity.Room;
import ru.eugene.planningpoker.impl.WebSocketEventListener;
import ru.eugene.planningpoker.impl.service.PokerUserService;
import ru.eugene.planningpoker.impl.service.RoomService;

import java.util.*;

@RestController
@AllArgsConstructor
public class WebSocketController {

    @Autowired
    private PokerUserService pokerUserService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private WebSocketEventListener webSocketEventListener;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/join/{roomId}")
    @Transactional
    public void joinRoom(@DestinationVariable final String roomId,
                         @Payload final Map<String, String> payload,
                         final SimpMessageHeaderAccessor headerAccessor) {
        final String name = payload.get("name");
        final String userId = payload.get("userId");
        final String sessionId = headerAccessor.getSessionId();

        System.out.println("joinRoom вызван с name: " + name + ", roomId: " + roomId + ", userId: " + userId);

        final Optional<Room> optionalRoom = roomService.findByRoomId(roomId);
        if (optionalRoom.isPresent()) {
            final Room room = optionalRoom.get();
            roomService.restoreRoomAndLeader(room); // Восстанавливаем комнату и лидера, если необходимо

            final List<PokerUser> usersInRoom = pokerUserService.findUsersByRoom(room);

            final Optional<PokerUser> existingUser = pokerUserService.findByUserId(userId);
            final PokerUser user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setName(name);
                user.setSessionId(sessionId);
                user.setRoom(room);
                pokerUserService.saveUser(user);
            } else {
                final boolean isLeader = usersInRoom.isEmpty();
                user = pokerUserService.createUser(name, room, sessionId, userId, isLeader);
            }

            headerAccessor.getSessionAttributes().put("sessionId", sessionId);
            headerAccessor.getSessionAttributes().put("roomId", roomId);
            headerAccessor.getSessionAttributes().put("userId", userId);

            webSocketEventListener.sendUpdatedUserList(room);
        } else {
            System.out.println("Комната не найдена: " + roomId);
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", "Комната не найдена");
        }
    }


    @MessageMapping("/number/{roomId}")
    @Transactional
    public void receiveNumber(@DestinationVariable final String roomId,
                              @Payload final Map<String, String> payload,
                              final SimpMessageHeaderAccessor headerAccessor) {
        final String selectedNumber = payload.get("selectedNumber");
        final String sessionId = headerAccessor.getSessionId();
        System.out.println("Получено число " + selectedNumber + " от сессии " + sessionId);

        final Optional<PokerUser> optionalUser = pokerUserService.findBySessionId(sessionId);
        if (optionalUser.isPresent()) {
            final PokerUser user = optionalUser.get();
            pokerUserService.updateSelectedNumber(user, selectedNumber);

            // Отправляем обновленный список пользователей всем в комнате
            final Room room = user.getRoom();
            webSocketEventListener.sendUpdatedUserList(room);
        }
    }

    @MessageMapping("/reveal/{roomId}")
    @Transactional
    public void revealVotes(@DestinationVariable final String roomId) {
        final Optional<Room> optionalRoom = roomService.findByRoomId(roomId);
        if (optionalRoom.isPresent()) {
            final Room room = optionalRoom.get();
            room.setVotingComplete(true);
            roomService.saveRoom(room);

            // Отправляем обновленный список пользователей всем в комнате
            webSocketEventListener.sendUpdatedUserList(room);
        }
    }

    @MessageMapping("/reset/{roomId}")
    @Transactional
    public void resetVotes(@DestinationVariable final String roomId) {
        final Optional<Room> optionalRoom = roomService.findByRoomId(roomId);
        if (optionalRoom.isPresent()) {
            final Room room = optionalRoom.get();
            final List<PokerUser> users = pokerUserService.findUsersByRoom(room);

            // Сбрасываем выбранные числа у всех пользователей
            for (final PokerUser user : users) {
                user.setSelectedNumber(null);
                pokerUserService.updateSelectedNumber(user, null);
            }

            room.setVotingComplete(false);
            roomService.saveRoom(room);

            // Отправляем обновленный список пользователей всем в комнате
            webSocketEventListener.sendUpdatedUserList(room);
        }
    }

    @MessageMapping("/mood/{roomId}")
    @Transactional
    public void updateMood(@DestinationVariable final String roomId,
                           @Payload final Map<String, String> payload,
                           final SimpMessageHeaderAccessor headerAccessor) {
        final String mood = payload.get("mood");
        final String sessionId = headerAccessor.getSessionId();

        final Optional<PokerUser> optionalUser = pokerUserService.findBySessionId(sessionId);
        if (optionalUser.isPresent()) {
            final PokerUser user = optionalUser.get();
            final Room userRoom = user.getRoom();

            // Проверяем, что roomId из URL совпадает с комнатой пользователя
            if (userRoom.getRoomId().equals(roomId)) {
                user.setMood(mood);
                pokerUserService.saveUser(user);

                // Отправляем обновленный список пользователей всем в комнате
                webSocketEventListener.sendUpdatedUserList(userRoom);
            } else {
                // Отправляем ошибку пользователю, если roomId не совпадает
                messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", "Неверный ID комнаты");
            }
        } else {
            // Отправляем ошибку пользователю, если пользователь не найден
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", "Пользователь не найден");
        }
    }

    @MessageExceptionHandler
    public void handleException(final Throwable exception, final SimpMessageHeaderAccessor headerAccessor) {
        final String sessionId = headerAccessor.getSessionId();
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", exception.getMessage() == null ? "ERROR EXCEPTION" : exception.getMessage());
    }
}
