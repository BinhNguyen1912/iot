package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.Telemetry;

import java.util.List;

public interface TelemetryService {

    // Lấy telemetry theo thiết bị
    List<Telemetry> getTelemetryByDevice(Long deviceId);

    // Lưu telemetry mới (dùng cho MQTT message)
    Telemetry saveTelemetry(Telemetry telemetry);
}
