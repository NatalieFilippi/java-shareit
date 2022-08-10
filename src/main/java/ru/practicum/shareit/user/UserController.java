package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") long id) throws ObjectNotFoundException {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody User user)  {
        return userService.create(user);
    }

    @PutMapping
    public User put(@RequestBody User user) throws ObjectNotFoundException {
        return userService.put(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) throws ObjectNotFoundException {
        userService.delete(userId);
    }
}
