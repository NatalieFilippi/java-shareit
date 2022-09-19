package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class UserTests {

    private static User user = new User(1, "Peter", "peter@ya.ru");

    private static UserDto userDto = new UserDto(1, "Peter", "peter@ya.ru");

    private UserService userService;
    private UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    private void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void findAllOk() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> users = userService.findAll();
        Assertions.assertEquals(users.size(), 1);
        Assertions.assertEquals(users.get(0).getName(), userDto.getName());
        Assertions.assertEquals(users.get(0).getEmail(), userDto.getEmail());
    }

    @Test
    void findByIdOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        UserDto getUser = userService.findById(1);
        Assertions.assertEquals(getUser.getName(), userDto.getName());
        Assertions.assertEquals(getUser.getEmail(), userDto.getEmail());
    }

    @Test
    void findByIdFail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> userService.findById(0));
        Assertions.assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void createOk() {
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto getUser = userService.create(new UserDto());
        Assertions.assertEquals(getUser.getName(), userDto.getName());
        Assertions.assertEquals(getUser.getEmail(), userDto.getEmail());
    }

    @Test
    void updateOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto getUser = userService.update(1, new UserDto());
        Assertions.assertEquals(getUser.getName(), userDto.getName());
    }

    @Test
    void updateUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> userService.update(0, new UserDto()));
        Assertions.assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void mapperToUser() {
        User getUser = UserMapper.toUser(userDto);
        Assertions.assertEquals(getUser.getName(), userDto.getName());
        Assertions.assertEquals(getUser.getEmail(), userDto.getEmail());
        Assertions.assertEquals(getUser.getId(), userDto.getId());
        getUser = UserMapper.toUser(null);
        Assertions.assertNull(getUser);
    }
    @Test
    void mapperToUserDto() {
        UserDto getUserDto = UserMapper.toUserDto(user);
        Assertions.assertEquals(getUserDto.getName(), user.getName());
        Assertions.assertEquals(getUserDto.getEmail(), user.getEmail());
        Assertions.assertEquals(getUserDto.getId(), user.getId());
        getUserDto = UserMapper.toUserDto(null);
        Assertions.assertNull(getUserDto);
    }

}
