package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.Device;

import java.util.List;

public interface DeviceService {

    List<Device> getAllDevices();

    Device createDevice(Device device);

    void controlDevice(Long id, String payload);
}
