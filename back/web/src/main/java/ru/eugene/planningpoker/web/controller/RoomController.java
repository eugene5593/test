package ru.eugene.planningpoker.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.eugene.planningpoker.api.entity.Room;
import ru.eugene.planningpoker.impl.service.RoomService;

import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestParam(required = false) final String roomId) {
        final Room room = roomService.createRoom(roomId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable final String roomId) {
        final Optional<Room> room = roomService.findByRoomId(roomId);
        return room.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
