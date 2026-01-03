package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.model.Room;
import com.nguyenanhbinh.lab306new.model.SmartDevice;
import com.nguyenanhbinh.lab306new.repository.RoomRepository;
import com.nguyenanhbinh.lab306new.repository.SmartDeviceRepository;
import com.nguyenanhbinh.lab306new.service.MqttPublisherService;
import com.nguyenanhbinh.lab306new.service.SmartDeviceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartDeviceServiceImpl implements SmartDeviceService {

    private final SmartDeviceRepository smartDeviceRepository;
    private final RoomRepository roomRepository;
    private final MqttPublisherService mqttPublisherService;

    public SmartDeviceServiceImpl(
            SmartDeviceRepository smartDeviceRepository,
            RoomRepository roomRepository,
            MqttPublisherService mqttPublisherService) {
        this.smartDeviceRepository = smartDeviceRepository;
        this.roomRepository = roomRepository;
        this.mqttPublisherService = mqttPublisherService;
    }

    @Override
    public SmartDevice createDevice(
            Long roomId,
            String name,
            String type,
            String image,
            String protocol,
            String topic) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // ✅ 1. Check device đã tồn tại theo name trong room
        return smartDeviceRepository.findByRoomAndName(room, name)
                .orElseGet(() -> {
                    // ✅ 2. Nếu chưa tồn tại → tạo mới
                    SmartDevice device = new SmartDevice();
                    device.setName(name);
                    device.setType(type);
                    device.setImage(image);
                    device.setProtocol(protocol);
                    device.setTopic(topic);
                    device.setOn(false);
                    device.setRoom(room);

                    return smartDeviceRepository.save(device);
                });
    }

    @Override
    public List<SmartDevice> getDevicesByRoom(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return smartDeviceRepository.findByRoom(room);
    }

    @Override
    public SmartDevice toggleDevice(Long deviceId, boolean isOn) {

        SmartDevice device = smartDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // 1. Update state
        device.setOn(isOn);
        smartDeviceRepository.save(device);

        // 2. Publish MQTT
        String payload = isOn ? "ON" : "OFF";
        mqttPublisherService.publish(device.getTopic(), payload);

        return device;
    }
}
