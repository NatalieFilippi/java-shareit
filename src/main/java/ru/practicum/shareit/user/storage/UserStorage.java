package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();
    User findById(long id) throws ObjectNotFoundException;
    User create(User user);
    User put(User user) throws  ObjectNotFoundException;
    void deleteAll();
    void delete(long userId) throws ObjectNotFoundException;
}
