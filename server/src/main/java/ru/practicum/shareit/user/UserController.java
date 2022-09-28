package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") long id) throws ObjectNotFoundException {
        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        return userService.create(user);
    }


    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long id, @RequestBody UserDto user) throws ObjectNotFoundException {
        return userService.update(id, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) throws ObjectNotFoundException {
        userService.delete(userId);
    }
}
