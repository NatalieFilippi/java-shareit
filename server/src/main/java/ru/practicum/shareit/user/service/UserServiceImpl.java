package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final String NOT_FOUND = "Не найден пользователь ";

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long id) throws ObjectNotFoundException {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id)));
    }

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto update(long id, UserDto userUpdate) throws ObjectNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
        if (userUpdate.getName() != null && !userUpdate.getName().isBlank()) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().isBlank()) {
            user.setEmail(userUpdate.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public void delete(long userId) throws ObjectNotFoundException {
        userRepository.deleteById(userId);
    }


}
