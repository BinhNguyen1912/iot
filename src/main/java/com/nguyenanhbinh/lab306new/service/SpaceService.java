package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.Space;
import com.nguyenanhbinh.lab306new.model.User;

import java.util.List;

public interface SpaceService {

    Space createSpace(String name, User owner);

    List<Space> getSpacesByUser(User owner);
}
