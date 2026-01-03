package com.nguyenanhbinh.lab306new.model;

import jakarta.persistence.*;

@Entity
@Table(name = "smart_devices")
public class SmartDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UI display
    @Column(nullable = false)
    private String name; // Smart Lamp

    private String type; // lamp, fan, switch

    private String image; // bulb, fan_icon

    // connection
    @Column(nullable = false)
    private String protocol; // WIFI, BLE

    // MQTT
    @Column(nullable = false)
    private String topic; // home/1/living/lamp/1

    // UI state
    private boolean isOn;

    // context
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // ===== GETTERS / SETTERS =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
