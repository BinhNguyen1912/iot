package com.nguyenanhbinh.lab306new.service.impl;

import com.nguyenanhbinh.lab306new.model.Space;
import com.nguyenanhbinh.lab306new.model.User;
import com.nguyenanhbinh.lab306new.repository.SpaceRepository;
import com.nguyenanhbinh.lab306new.service.SpaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;

    public SpaceServiceImpl(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @Override
    public Space createSpace(String name, User owner) {
        Space space = new Space();
        space.setName(name);
        space.setOwner(owner);
        return spaceRepository.save(space);
    }

    @Override
    public List<Space> getSpacesByUser(User owner) {
        return spaceRepository.findByOwner(owner);
    }
}
