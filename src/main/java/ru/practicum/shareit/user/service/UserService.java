package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAll();
    User findById(long id) throws ObjectNotFoundException;
    User create(User user);
    User update(long id, Map<String, String> fields) throws ObjectNotFoundException;
    void deleteAll();
    void delete(long userId) throws  ObjectNotFoundException;
}
