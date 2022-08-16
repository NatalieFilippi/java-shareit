package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    public User create(@Valid @RequestBody User user)  {
        return userService.create(user);
    }


    @PatchMapping("/{id}")
    public User update(@PathVariable("id") long id, @RequestBody Map<String,String> fields) throws ObjectNotFoundException {
        return userService.update(id, fields);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") long userId) throws ObjectNotFoundException {
        userService.delete(userId);
    }
}
