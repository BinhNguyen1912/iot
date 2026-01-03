package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.SmartDevice;
import com.nguyenanhbinh.lab306new.service.SmartDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart-devices")
public class SmartDeviceController {

    private final SmartDeviceService smartDeviceService;

    public SmartDeviceController(SmartDeviceService smartDeviceService) {
        this.smartDeviceService = smartDeviceService;
    }

    // =========================
    // ADD DEVICE (Add Device UI)
    // =========================
    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody Map<String, String> request) {

        SmartDevice device = smartDeviceService.createDevice(
                Long.valueOf(request.get("roomId")),
                request.get("name"),
                request.get("type"),
                request.get("image"),
                request.get("protocol"),
                request.get("topic"));

        return ResponseEntity.ok(
                Map.of("success", true, "deviceId", device.getId()));
    }

    // =========================
    // GET DEVICES BY ROOM
    // =========================
    @GetMapping
    public ResponseEntity<List<SmartDevice>> getDevices(
            @RequestParam Long roomId) {

        return ResponseEntity.ok(
                smartDeviceService.getDevicesByRoom(roomId));
    }

    // =========================
    // TOGGLE DEVICE (ON / OFF)
    // =========================
    @PostMapping("/{id}/toggle")
    public ResponseEntity<?> toggleDevice(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        boolean isOn = request.get("isOn");

        SmartDevice device = smartDeviceService.toggleDevice(id, isOn);

        return ResponseEntity.ok(
                Map.of("success", true, "isOn", device.isOn()));
    }
}
