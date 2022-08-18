package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
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
    public List<UserDto> findAll() {
        return userStorage.findAll();
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        return userStorage.findById(id);
    }

    @Override
    public UserDto create(User user) {
        return userStorage.create(user);
    }

    @Override
    public UserDto update(long id, Map<String, String> fields) throws ObjectNotFoundException {
        UserDto user = userStorage.findById(id);
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
