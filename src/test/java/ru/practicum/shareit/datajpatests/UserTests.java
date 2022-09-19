package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
public class UserTests {

    @Autowired
    UserRepository repository;
    @Autowired
    private TestEntityManager em;
    private static User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("Peter");
        user.setEmail("peter@ya.ru");
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveUser() {
        repository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getName(), "Peter");
    }

    @Test
    void updateUser() {
        repository.save(user);
        user.setEmail("peter2@ya.ru");
        repository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getEmail(),"peter2@ya.ru");
    }

}
