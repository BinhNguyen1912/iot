package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.Device;
import com.nguyenanhbinh.lab306new.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    // lấy danh sách thiết bị
    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    // thêm thiết bị mới
    @PostMapping
    public Device createDevice(@RequestBody Device device) {
        return deviceService.createDevice(device);
    }

    // gửi lệnh điều khiển thiết bị
    @PostMapping("/{id}/control")
    public String controlDevice(@PathVariable Long id,
            @RequestBody String payload) {
        deviceService.controlDevice(id, payload);
        return "Command sent";
    }
}
