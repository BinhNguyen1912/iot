package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.Room;

import java.util.List;

public interface RoomService {

    Room createRoom(Long spaceId, String name);

    List<Room> getRoomsBySpace(Long spaceId);
}
