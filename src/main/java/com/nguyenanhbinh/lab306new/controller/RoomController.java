package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.Room;
import com.nguyenanhbinh.lab306new.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // =========================
    // ADD ROOM (Add Rooms step)
    // =========================
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Map<String, String> request) {

        Long spaceId = Long.valueOf(request.get("spaceId"));
        String name = request.get("name");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Room name is required"));
        }

        Room room = roomService.createRoom(spaceId, name);

        return ResponseEntity.ok(
                Map.of("success", true, "roomId", room.getId()));
    }

    // =========================
    // GET ROOMS BY SPACE
    // =========================
    @GetMapping
    public ResponseEntity<List<Room>> getRooms(@RequestParam Long spaceId) {

        return ResponseEntity.ok(
                roomService.getRoomsBySpace(spaceId));
    }
}
