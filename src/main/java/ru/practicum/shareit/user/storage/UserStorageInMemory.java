package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class UserStorageInMemory implements UserStorage {
    private static final String INVALID_EMAIL = "Пользователь с указанным email уже существует.";
    private static final String NO_EMAIL = "Не указан email пользователя.";
    private static final String NO_NAME = "Не указано имя пользователя.";
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
        if (validateUser(user)) {
            user.setId(getNextId());
            users.put(user.getId(), user) ;
            log.debug("Сохранён пользователь: {}", user.toString());
        }
        return user;
    }

    @Override
    public User update(User user, Map<String,String> fields) throws ObjectNotFoundException {
        User userCopy = new User(user);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, (String) key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, userCopy, value);
        });
        if (validateUser(userCopy)) {
            users.put(user.getId(), userCopy) ;
            log.debug("Обновлён пользователь: {}", userCopy.toString());
        }
        return userCopy;
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

    private boolean validateUser(User user) {
        List<User> users = findAll();
        List<User> foundUser = users.stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))   //найденный элемент имеет такой же email
                .filter(u -> u.getId() != user.getId())         //найденный элемент не текущий
                .collect(Collectors.toList());
        if (foundUser == null || foundUser.isEmpty()) {
            return true;
        }
        log.debug("Ошибка при попытке добавления пользователя: " + INVALID_EMAIL);
        throw new ConflictException(INVALID_EMAIL);
    }
}
