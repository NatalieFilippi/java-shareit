package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class BookingTests {

    private static LocalDateTime start = LocalDateTime.now();
    private static LocalDateTime end = LocalDateTime.now().plusHours(2);
    private static UserDto userDto;
    private static UserDto userBooker;
    private static Item item;
    private static Booking booking;
    private static BookingDto bookingDto;
    private BookingService bookingService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private BookingRepository mockBookingRepository;

    @BeforeEach
    private void beforeEach() {
        userDto = UserDto.builder()
                .id(1)
                .name("Peter")
                .email("peter@ya.ru")
                .build();
        userBooker = UserDto.builder()
                .id(2)
                .name("Lev")
                .email("lev@ya.ru")
                .build();
        item = Item.builder()
                .id(1)
                .name("вещь")
                .description("Описание вещи")
                .owner(1)
                .isAvailable(true)
                .request(0)
                .build();
        booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(UserMapper.toUser(userBooker))
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .itemId(1)
                .booker(2)
                .status(BookingStatus.WAITING)
                .build();
        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemRepository, mockUserService);
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(booking));
    }

    @Test
    void createOk() {
        Mockito
                .when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto dto = bookingService.create(2L, bookingDto);
        Assertions.assertEquals(dto.getId(), booking.getId());
        Assertions.assertEquals(dto.getBooker(), booking.getBooker().getId());
        Assertions.assertEquals(dto.getItemId(), booking.getItem().getId());
        Assertions.assertEquals(dto.getStart(), booking.getStart());
        Assertions.assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    void createNotAvailable() {
        item.setAvailable(false);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(2L, bookingDto));
        Assertions.assertEquals(exception.getMessage(), "Вещь не доступна для бронирования.");
    }

    @Test
    void createFailDate() {
        bookingDto.setEnd(start.minusDays(1));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(2L, bookingDto));
        Assertions.assertEquals(exception.getMessage(), "Время окончания бронирования должно быть позже времени начала бронирования.");
    }

    @Test
    void createOwnerIsBooking() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.create(1L, bookingDto));
        Assertions.assertEquals(exception.getMessage(), "Владедец не может забронировать свою вещь.");
    }

    @Test
    void updateOk() {
        Mockito
                .when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingRequestDto dto = bookingService.update(1L, true, 1L);
        Assertions.assertEquals(dto.getId(), booking.getId());
    }

    @Test
    void updateNotOwner() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.update(2L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Редактирование доступно только для владельца.");
    }

    @Test
    void updateRepeatConfirmation() {
        booking.setStatus(BookingStatus.APPROVED);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(1L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Бронирование уже подтверждено.");
    }

    @Test
    void updateNotFoundBooking() {
        Mockito
                .when((mockBookingRepository.findById(Mockito.anyLong())))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.update(1L, true, 1L));
        Assertions.assertEquals(exception.getMessage(), "Бронирование не найдено: 1");
    }

    @Test
    void findByIdOk() {
        BookingRequestDto dto = bookingService.findById(1L, 1L);
        Assertions.assertEquals(dto.getId(), booking.getId());
    }

    @Test
    void findByIdNoAccess() {
        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.findById(3L, 1L));
        Assertions.assertEquals(exception.getMessage(), "Просмотр бронирования доступен для владельца вещи или автора бронирования.");
    }

    @Test
    void findAllByIdAll() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBooker_Id(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.ALL, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdWAITING() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndStatus(2L, BookingStatus.WAITING, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.WAITING, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void findAllByIdREJECTED() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndStatus(2L, BookingStatus.REJECTED, pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.REJECTED, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void findAllByIdPAST() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndEndBefore(Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.PAST, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdFUTURE() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndStartAfter(Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.FUTURE, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByIdCURRENT() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(userBooker);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllById(2L, BookingState.CURRENT, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerAll() {
        Mockito
                .when(mockBookingRepository.findAllByItem_IdIn(Mockito.anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.ALL, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerWAITING() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItem_IdInAndStatus(1L, BookingStatus.WAITING.toString(), pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.WAITING, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerREJECTED() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItem_IdInAndStatus(1L, BookingStatus.REJECTED.toString(), pageable))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.REJECTED, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerPAST() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItem_IdInAndEndBefore(Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.PAST, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerFUTURE() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItem_IdInAndStartAfter(Mockito.anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.FUTURE, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void findAllByOwnerCURRENT() {
        Pageable pageable = PageRequest.of(0, 10);
        Mockito
                .when(mockBookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingRequestDto> dtos = bookingService.findAllByOwner(1L, BookingState.CURRENT, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

}
