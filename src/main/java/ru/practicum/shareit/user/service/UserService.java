package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long id) throws ObjectNotFoundException;

    UserDto create(UserDto user);

    UserDto update(long id, UserDto user) throws ObjectNotFoundException;

    void deleteAll();

    void delete(long userId) throws ObjectNotFoundException;
}
