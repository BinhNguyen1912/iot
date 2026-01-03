package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.model.Room;
import com.nguyenanhbinh.lab306new.model.Space;
import com.nguyenanhbinh.lab306new.repository.RoomRepository;
import com.nguyenanhbinh.lab306new.repository.SpaceRepository;
import com.nguyenanhbinh.lab306new.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final SpaceRepository spaceRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
            SpaceRepository spaceRepository) {
        this.roomRepository = roomRepository;
        this.spaceRepository = spaceRepository;
    }

    @Override
    public Room createRoom(Long spaceId, String name) {

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        Room room = new Room();
        room.setName(name);
        room.setSpace(space);

        return roomRepository.save(room);
    }

    @Override
    public List<Room> getRoomsBySpace(Long spaceId) {

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        return roomRepository.findBySpace(space);
    }
}
