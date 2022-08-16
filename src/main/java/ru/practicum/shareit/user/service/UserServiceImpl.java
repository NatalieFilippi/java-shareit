package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findById(long id) throws ObjectNotFoundException {
        return userStorage.findById(id);
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(long id, Map<String, String> fields) throws ObjectNotFoundException {
        User user = userStorage.findById(id);
        if (user == null) {
            log.debug("Пользователь не найден: {}", user.getId());
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        return userStorage.update(user, fields);
    }

    @Override
    public void deleteAll() {
        userStorage.deleteAll();
    }

    @Override
    public void delete(long userId) throws ObjectNotFoundException {
        userStorage.delete(userId);
    }



}