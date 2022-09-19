package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingTests {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService service;
    private static ItemDto itemDto;
    private static UserDto userOwner;
    private static UserDto userBooker;
    private static BookingDto bookingDto;
    private static LocalDateTime start = LocalDateTime.now();
    private static LocalDateTime end = LocalDateTime.now().plusHours(2);

    @BeforeEach
    @Sql({"/schema.sql"})
    public void setUp() {
        itemDto = ItemDto.builder()
                .id(1)
                .name("вещь")
                .description("Описание вещи")
                .owner(1)
                .available(true)
                .request(0)
                .build();
        userOwner = UserDto.builder()
                .name("Peter")
                .email("peter@ya.ru")
                .build();
        userBooker = UserDto.builder()
                .name("Lev")
                .email("lev@ya.ru")
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(1)
                .booker(2)
                .status(BookingStatus.WAITING)
                .build();
    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }

    @Test
    void save() {
        UserDto userOwnerDto = userService.create(userOwner);
        UserDto userBookerDto = userService.create(userBooker);
        itemDto.setOwner(userOwnerDto.getId());
        ItemDto getItem = itemService.create(userOwnerDto.getId(), itemDto);
        bookingDto.setBooker(userBookerDto.getId());
        bookingDto.setItemId(getItem.getId());
        BookingDto getBooking = service.create(userBookerDto.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking booking = query.setParameter("id", userBookerDto.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
    }

    @Test
    void update() {
        UserDto userOwnerDto = userService.create(userOwner);
        UserDto userBookerDto = userService.create(userBooker);
        itemDto.setOwner(userOwnerDto.getId());
        ItemDto getItem = itemService.create(userOwnerDto.getId(), itemDto);
        bookingDto.setBooker(userBookerDto.getId());
        bookingDto.setItemId(getItem.getId());
        BookingDto getBooking = service.create(userBookerDto.getId(), bookingDto);
        service.update(userOwnerDto.getId(), true, getBooking.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking booking = query.setParameter("id", userBookerDto.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
    }

    @Test
    void findById() {
        UserDto userOwnerDto = userService.create(userOwner);
        UserDto userBookerDto = userService.create(userBooker);
        itemDto.setOwner(userOwnerDto.getId());
        ItemDto getItem = itemService.create(userOwnerDto.getId(), itemDto);
        bookingDto.setBooker(userBookerDto.getId());
        bookingDto.setItemId(getItem.getId());
        BookingDto getBooking = service.create(userBookerDto.getId(), bookingDto);
        BookingRequestDto booking = service.findById(userOwnerDto.getId(), getBooking.getId());

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getItemDto().getName(), equalTo(itemDto.getName()));
        assertThat(booking.getBooker().getName(), equalTo(userBooker.getName()));
    }

    @Test
    void findAllById() {
        UserDto userOwnerDto = userService.create(userOwner);
        UserDto userBookerDto = userService.create(userBooker);
        itemDto.setOwner(userOwnerDto.getId());
        ItemDto getItem = itemService.create(userOwnerDto.getId(), itemDto);
        bookingDto.setBooker(userBookerDto.getId());
        bookingDto.setItemId(getItem.getId());
        BookingDto getBooking = service.create(userBookerDto.getId(), bookingDto);

        List<BookingRequestDto> bookings = service.findAllById(userBookerDto.getId(), BookingState.ALL, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllById(userBookerDto.getId(), BookingState.WAITING, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllById(userBookerDto.getId(), BookingState.REJECTED, 1, 10);
        assertThat(bookings.size(), equalTo(0));

        bookings = service.findAllById(userBookerDto.getId(), BookingState.PAST, 1, 10);
        assertThat(bookings.size(), equalTo(0));

        bookings = service.findAllById(userBookerDto.getId(), BookingState.CURRENT, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllById(userBookerDto.getId(), BookingState.FUTURE, 1, 10);
        assertThat(bookings.size(), equalTo(0));
    }

    @Test
    void findAllByOwner() {
        UserDto userOwnerDto = userService.create(userOwner);
        UserDto userBookerDto = userService.create(userBooker);
        itemDto.setOwner(userOwnerDto.getId());
        ItemDto getItem = itemService.create(userOwnerDto.getId(), itemDto);
        bookingDto.setBooker(userBookerDto.getId());
        bookingDto.setItemId(getItem.getId());
        BookingDto getBooking = service.create(userBookerDto.getId(), bookingDto);

        List<BookingRequestDto> bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.ALL, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.WAITING, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.REJECTED, 1, 10);
        assertThat(bookings.size(), equalTo(0));

        bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.PAST, 1, 10);
        assertThat(bookings.size(), equalTo(0));

        bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.CURRENT, 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = service.findAllByOwner(userOwnerDto.getId(), BookingState.FUTURE, 1, 10);
        assertThat(bookings.size(), equalTo(0));
    }
}
