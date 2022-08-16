package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> findAll();

    User findById(long id) throws ObjectNotFoundException;

    User create(User user);

    User update(User user, Map<String, String> fields) throws ObjectNotFoundException;

    void deleteAll();

    void delete(long userId) throws ObjectNotFoundException;
}
