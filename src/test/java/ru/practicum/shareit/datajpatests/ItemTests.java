package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
public class ItemTests {

    @Autowired
    ItemRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TestEntityManager em;
    private static Item item;
    private static User user;

    @BeforeEach
    void beforeEach() {
        item = Item.builder()
                .name("вещь")
                .description("Описание вещи")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();

        user = User.builder()
                .name("Peter")
                .email("peter@ya.ru")
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveItem() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getName(), "вещь");
    }

    @Test
    void search() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Page<Item> items = repository.search("щь", Pageable.unpaged());
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.getSize(), 1);
    }

    @Test
    void findAllByOwnerOrderById() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        List<Item> items = repository.findAllByOwnerOrderById(user.getId());
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.size(), 1);
        Assertions.assertEquals(items.get(0).getName(), "вещь");
    }

    @Test
    void findIdByOwner() {
        userRepository.save(user);
        item.setOwner(user.getId());
        repository.save(item);
        Page<Long> ids = repository.findIdByOwner(user.getId(), Pageable.unpaged());
        Assertions.assertNotNull(ids);
        Assertions.assertEquals(ids.getSize(), 1);
    }

    @Test
    void findByRequest_Id() {
        userRepository.save(user);
        item.setOwner(user.getId());
        item.setRequest(8);
        repository.save(item);
        List<Item> items = repository.findByRequest_Id(8);
        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.size(), 1);
    }

}
