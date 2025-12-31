package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.PowerData;
import com.nguyenanhbinh.lab306new.repository.PowerDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PowerDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerDataService.class);

    private final PowerDataRepository powerDataRepository;

    // Lưu dữ liệu mới nhất trong memory (cho API nhanh)
    private PowerData latestPowerData;

    public PowerDataService(PowerDataRepository powerDataRepository) {
        this.powerDataRepository = powerDataRepository;
    }

    /**
     * Lưu dữ liệu công suất mới
     */
    public PowerData savePowerData(Double current, Double power, Integer relay) {
        PowerData data = new PowerData(current, power, relay);

        // Lưu vào DB
        PowerData saved = powerDataRepository.save(data);

        // Cập nhật cache
        this.latestPowerData = saved;

        LOGGER.info("Saved power data: I={} A, P={} W, Relay={}", current, power, relay);

        return saved;
    }

    /**
     * Lấy dữ liệu mới nhất (từ cache hoặc DB)
     */
    public PowerData getLatestPowerData() {
        if (latestPowerData != null) {
            return latestPowerData;
        }

        // Nếu chưa có cache, load từ DB
        Optional<PowerData> latest = powerDataRepository.findLatest();
        if (latest.isPresent()) {
            latestPowerData = latest.get();
            return latestPowerData;
        }

        // Nếu DB trống, trả về giá trị mặc định
        return new PowerData(0.0, 0.0, 0);
    }
}
