package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<UserDto> findAll();

    UserDto findById(long id) throws ObjectNotFoundException;

    UserDto create(User user);

    UserDto update(UserDto user, Map<String, String> fields) throws ObjectNotFoundException;

    void deleteAll();

    void delete(long userId) throws ObjectNotFoundException;
}
