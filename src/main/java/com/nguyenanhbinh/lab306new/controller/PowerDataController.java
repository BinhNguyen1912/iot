package com.nguyenanhbinh.lab306new.controller;

import com.nguyenanhbinh.lab306new.model.PowerData;
import com.nguyenanhbinh.lab306new.service.PowerDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/power")
@CrossOrigin(origins = "*") // Cho phép Flutter gọi API
public class PowerDataController {

    private final PowerDataService powerDataService;

    public PowerDataController(PowerDataService powerDataService) {
        this.powerDataService = powerDataService;
    }

    /**
     * API: Lấy dữ liệu công suất mới nhất
     * GET http://192.168.2.75:8080/power/latest
     */
    @GetMapping("/latest")
    public PowerData getLatestPowerData() {
        return powerDataService.getLatestPowerData();
    }
}
