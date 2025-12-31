package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.Telemetry;
import com.nguyenanhbinh.lab306new.service.TelemetryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    // Lấy toàn bộ dữ liệu telemetry theo thiết bị
    @GetMapping("/{deviceId}")
    public List<Telemetry> getByDevice(@PathVariable Long deviceId) {
        return telemetryService.getTelemetryByDevice(deviceId);
    }
}
