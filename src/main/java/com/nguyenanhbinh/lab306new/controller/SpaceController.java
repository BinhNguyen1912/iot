package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.Space;
import com.nguyenanhbinh.lab306new.model.User;
import com.nguyenanhbinh.lab306new.service.SpaceService;
import com.nguyenanhbinh.lab306new.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceService spaceService;
    private final UserService userService;

    public SpaceController(SpaceService spaceService, UserService userService) {
        this.spaceService = spaceService;
        this.userService = userService;
    }

    // =========================
    // CREATE SPACE (Add Home Name)
    // =========================
    @PostMapping
    public ResponseEntity<?> createSpace(@RequestBody Map<String, String> request) {

        String username = request.get("username"); // username == email
        String name = request.get("name");

        if (username == null || name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Missing fields"));
        }

        User user = userService.findOrCreateByEmail(username);

        Space space = spaceService.createSpace(name, user);

        return ResponseEntity.ok(
                Map.of("success", true, "spaceId", space.getId()));
    }

    // =========================
    // GET SPACES BY USER
    // =========================
    @GetMapping
    public ResponseEntity<?> getSpaces(@RequestParam String username) {

        User user = userService.findOrCreateByEmail(username);

        List<Space> spaces = spaceService.getSpacesByUser(user);

        return ResponseEntity.ok(spaces);
    }
}
