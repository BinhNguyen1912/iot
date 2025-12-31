package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.mqtt.MqttTopicManager;
import com.nguyenanhbinh.lab306new.model.Device;
import com.nguyenanhbinh.lab306new.model.Telemetry;
import com.nguyenanhbinh.lab306new.repository.DeviceRepository;
import com.nguyenanhbinh.lab306new.service.DeviceService;
import com.nguyenanhbinh.lab306new.service.MqttPublisherService;
import com.nguyenanhbinh.lab306new.service.TelemetryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final MqttPublisherService mqttPublisherService;
    private final MqttTopicManager mqttTopicManager;
    private final TelemetryService telemetryService;

    public DeviceServiceImpl(DeviceRepository deviceRepository,
            MqttPublisherService mqttPublisherService,
            MqttTopicManager mqttTopicManager,
            TelemetryService telemetryService) {
        this.deviceRepository = deviceRepository;
        this.mqttPublisherService = mqttPublisherService;
        this.mqttTopicManager = mqttTopicManager;
        this.telemetryService = telemetryService;
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Device createDevice(Device device) {
        String topic = device.getTopic();

        // yêu cầu subscribe topic
        mqttTopicManager.subscribeIfNotExists(topic);

        return deviceRepository.save(device);
    }

    @Override
    public void controlDevice(Long id, String payload) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        mqttPublisherService.publish(device.getTopic(), payload);

        Telemetry telemetry = new Telemetry();
        telemetry.setDeviceId(device.getId());
        telemetry.setPayload(payload);
        telemetryService.saveTelemetry(telemetry);
    }
}
