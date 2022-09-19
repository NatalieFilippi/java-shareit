package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dao.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestTests {

    @Autowired
    ItemRequestRepository repository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TestEntityManager em;
    private static Item item;
    private static User userOwner;
    private static User user;
    private static ItemRequest itemRequest;
    private static LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        item = Item.builder()
                .name("вещь")
                .description("Описание вещи")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();

        userOwner = User.builder()
                .name("Peter")
                .email("peter@ya.ru")
                .build();

        user = User.builder()
                .name("Lev")
                .email("lev@ya.ru")
                .build();
        itemRequest = ItemRequest.builder()
                .description("Описание")
                .created(created)
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveRequest() {
        userRepository.save(userOwner);
        userRepository.save(user);
        itemRequest.setRequestor(user.getId());
        repository.save(itemRequest);

        Assertions.assertNotNull(itemRequest.getId());
        Assertions.assertEquals(itemRequest.getCreated(), created);
        Assertions.assertEquals(itemRequest.getDescription(), "Описание");
        Assertions.assertEquals(itemRequest.getRequestor(), user.getId());
    }

    @Test
    void findByRequestor() {
        userRepository.save(userOwner);
        userRepository.save(user);
        itemRequest.setRequestor(user.getId());
        repository.save(itemRequest);

        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        List<ItemRequest> requests = repository.findByRequestor(user.getId());

        Assertions.assertNotNull(requests);
        Assertions.assertEquals(requests.size(), 1);
        Assertions.assertEquals(requests.get(0).getDescription(), "Описание");
    }

    @Test
    void findAllByRequestorNot() {
        userRepository.save(userOwner);
        userRepository.save(user);
        itemRequest.setRequestor(user.getId());
        repository.save(itemRequest);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        Page<ItemRequest> requests = repository.findAllByRequestorNot(user.getId(), Pageable.unpaged());
        Assertions.assertEquals(requests.getSize(), 0);
    }

}
