package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.PowerData;
import com.nguyenanhbinh.lab306new.service.PowerDataService;
import com.nguyenanhbinh.lab306new.service.RelayControlService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/power")
@CrossOrigin(origins = "*")
public class PowerDataController {

    private final PowerDataService powerDataService;
    private final RelayControlService relayControlService;

    public PowerDataController(PowerDataService powerDataService,
            RelayControlService relayControlService) {
        this.powerDataService = powerDataService;
        this.relayControlService = relayControlService;
    }

    /**
     * ✅ API: Lấy dữ liệu công suất mới nhất
     * GET http://localhost:8080/power/latest
     */
    @GetMapping("/latest")
    public PowerData getLatestPowerData() {
        return powerDataService.getLatestPowerData();
    }

    /**
     * ✅ API: Điều khiển relay
     * POST http://localhost:8080/power/relay
     * Body: {"state": 1} // 1 = ON, 0 = OFF
     */
    @PostMapping("/relay")
    public Map<String, Object> controlRelay(@RequestBody Map<String, Integer> request) {
        Integer state = request.get("state");

        if (state == null || (state != 0 && state != 1)) {
            return Map.of(
                    "success", false,
                    "message", "Invalid state. Use 0 (OFF) or 1 (ON)");
        }

        relayControlService.setRelay(state);

        return Map.of(
                "success", true,
                "message", "Relay command sent",
                "state", state == 1 ? "ON" : "OFF");
    }

    /**
     * ✅ API: Bật relay
     * POST http://localhost:8080/power/relay/on
     */
    @PostMapping("/relay/on")
    public Map<String, String> turnOn() {
        relayControlService.turnOn();
        return Map.of("message", "Relay turned ON");
    }

    /**
     * ✅ API: Tắt relay
     * POST http://localhost:8080/power/relay/off
     */
    @PostMapping("/relay/off")
    public Map<String, String> turnOff() {
        relayControlService.turnOff();
        return Map.of("message", "Relay turned OFF");
    }
}