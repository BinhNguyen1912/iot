package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.SmartDevice;

import java.util.List;

public interface SmartDeviceService {

    SmartDevice createDevice(
            Long roomId,
            String name,
            String type,
            String image,
            String protocol,
            String topic);

    List<SmartDevice> getDevicesByRoom(Long roomId);

    SmartDevice toggleDevice(Long deviceId, boolean isOn);
}
