package ru.practicum.shareit.datajpatests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@DataJpaTest
public class BookingTests {

    @Autowired
    BookingRepository repository;
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
    private static User userBooker;
    private static Booking booking;
    private static LocalDateTime start = LocalDateTime.now();
    private static LocalDateTime end = LocalDateTime.now().plusHours(2);

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

        userBooker = User.builder()
                .name("Lev")
                .email("lev@ya.ru")
                .build();

        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(userBooker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void saveItem() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);
        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getStart(), start);
        Assertions.assertEquals(booking.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllByBooker_Id() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBooker_Id(userBooker.getId(), Pageable.unpaged());
        Assertions.assertNotNull(bookings);
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndEndBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBooker_IdAndEndBefore(userBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBooker_IdAndEndBefore(userBooker.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStartAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBooker_IdAndStartAfter(userBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBooker_IdAndStartAfter(userBooker.getId(), LocalDateTime.now().minusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBooker_IdAndStartBeforeAndEndAfter(userBooker.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByBooker_IdAndStatus(userBooker.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByBooker_IdAndStatus(userBooker.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdIn() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItem_IdIn(userOwner.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);

        bookings = repository.findAllByItem_IdIn(userBooker.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);
    }

    @Test
    void findAllByItem_IdInAndEndBefore() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItem_IdInAndEndBefore(userOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItem_IdInAndEndBefore(userOwner.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdInAndStartAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItem_IdInAndStartAfter(userOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItem_IdInAndStartAfter(userOwner.getId(), LocalDateTime.now().minusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdInAndStartBeforeAndEndAfter() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItem_IdInAndStartBeforeAndEndAfter(userOwner.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdInAndStatus() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Page<Booking> bookings = repository.findAllByItem_IdInAndStatus(userOwner.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = repository.findAllByItem_IdInAndStatus(userOwner.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }


    @Test
    void getByIdForResponse() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        item.setOwner(userOwner.getId());
        itemRepository.save(item);
        repository.save(booking);

        Comment comment = Comment.builder()
                .text("Отзыв")
                .item(item)
                .created(LocalDateTime.now())
                .author(userBooker)
                .build();
        commentRepository.save(comment);

        ItemDto itemDto = itemRepository.getByIdForResponse(userBooker.getId(), item.getId());
        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getComments().size(), 1);
    }


}
