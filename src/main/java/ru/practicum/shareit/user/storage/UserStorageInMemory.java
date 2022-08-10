package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Repository
public class UserStorageInMemory implements UserStorage {
    private long lastUsedId = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long id) throws ObjectNotFoundException {
        if (!users.containsKey(id)) {
            log.debug("Пользователь не найден: {}", id);
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Сохранён пользователь: {}", user.toString());
        return user;
    }

    @Override
    public User put(User user) throws ObjectNotFoundException {
        if (!users.containsKey(user.getId())) {
            log.debug("Пользователь не найден: {}", user.getId());
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        users.put(user.getId(), user);
        log.debug("Обновлён пользователь: {}", user.toString());
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public void delete(long userId) throws ObjectNotFoundException {
        log.debug("Удалён пользователь: {}", userId);
        users.remove(userId);
    }

    private long getNextId() {
        return ++lastUsedId;
    }
}
