package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.lang.reflect.Field;
import java.util.*;
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
    public List<UserDto> findAll() {
        return new ArrayList<>(users.values().stream().map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList()));
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        if (!users.containsKey(id)) {
            log.debug("Пользователь не найден: {}", id);
            throw new ObjectNotFoundException("Пользователь не найден.");
        }
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public UserDto create(User user) {
        if (validateUser(user)) {
            user.setId(getNextId());
            users.put(user.getId(), user);
            log.debug("Сохранён пользователь: {}", user.toString());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Map<String, String> fields) throws ObjectNotFoundException {
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(UserDto.class, (String) key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, userDto, value);
        });
        User user = UserMapper.toUser(userDto);
        if (validateUser(user)) {
            users.put(user.getId(), user);
            log.debug("Обновлён пользователь: {}", user.toString());
        }
        return userDto;
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
        Optional<User> foundUser = findAll().stream()
                .map(u -> UserMapper.toUser(u))
                .filter(u -> u.getEmail().equals(user.getEmail()))   //найденный элемент имеет такой же email
                .filter(u -> u.getId() != user.getId())         //найденный элемент не текущий
                .findAny();
        if (!foundUser.isPresent()) {
            return true;
        }
        log.debug("Ошибка при попытке добавления пользователя: " + INVALID_EMAIL);
        throw new ConflictException(INVALID_EMAIL);
    }
}
