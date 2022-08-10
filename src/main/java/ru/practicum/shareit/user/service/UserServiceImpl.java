package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

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
    public User put(User user) throws ObjectNotFoundException {
        return userStorage.put(user);
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
