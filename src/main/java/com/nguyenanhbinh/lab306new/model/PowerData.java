package com.nguyenanhbinh.lab306new.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "power_data")
public class PowerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double current; // Dòng điện (A)

    @Column(nullable = false)
    private Double power; // Công suất (W)

    @Column(nullable = false)
    private Integer relay; // Trạng thái relay (0/1)

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public PowerData() {
        this.timestamp = LocalDateTime.now();
    }

    public PowerData(Double current, Double power, Integer relay) {
        this.current = current;
        this.power = power;
        this.relay = relay;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Integer getRelay() {
        return relay;
    }

    public void setRelay(Integer relay) {
        this.relay = relay;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
