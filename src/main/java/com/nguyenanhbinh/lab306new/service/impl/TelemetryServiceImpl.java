package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.model.Telemetry;
import com.nguyenanhbinh.lab306new.repository.TelemetryRepository;
import com.nguyenanhbinh.lab306new.service.TelemetryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryServiceImpl(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    @Override
    public List<Telemetry> getTelemetryByDevice(Long deviceId) {
        return telemetryRepository.findByDeviceId(deviceId);
    }

    @Override
    public Telemetry saveTelemetry(Telemetry telemetry) {
        return telemetryRepository.save(telemetry);
    }
}
