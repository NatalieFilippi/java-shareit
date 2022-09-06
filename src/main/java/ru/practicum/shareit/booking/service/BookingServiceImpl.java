package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final static String NOT_FOUND = "Бронирование не найдено: ";

    public BookingServiceImpl(BookingRepository bookingRepository, ItemStorage itemStorage, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User user = getUser(userId);               //проверка на существование пользователя
        Item item = getItem(bookingDto.getItemId()); //проверка на существование вещи
        if (!item.isAvailable()) {
            log.debug("Вещь не доступна для бронивания.");
            throw new ObjectNotFoundException("Вещь не доступна для бронивания.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.debug("Время окончания бронирования должно быть позже времени начала бронирования.");
            throw new ValidationException("Время окончания бронирования должно быть позже времени начала бронирования.");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(userId);
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingDto)));
    }

    @Override
    public BookingDtoWithBookerAndItem update(Long owner, boolean approved, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = getItem(booking.getItem());
        User booker = getUser(booking.getBooker());
        if (item.getOwner() != owner) {
            log.debug("Редактирование доступно только для владельца: {}", owner);
            throw new ObjectNotFoundException("Редактирование доступно только для владельца.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            log.debug("Бронирование уже подтверждено.");
            throw new ValidationException("Бронирование уже подтверждено.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        BookingDtoWithBookerAndItem bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking, item, booker);
        return bookingResponse;
    }

    @Override
    public BookingDtoWithBookerAndItem findById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = getItem(booking.getItem());
        User booker = getUser(booking.getBooker());
        if (booking.getBooker() != userId && item.getOwner() != userId) {
            log.debug("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
            throw new ObjectNotFoundException("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
        }
        BookingDtoWithBookerAndItem bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking, item, booker);
        return bookingResponse;
    }

    @Override
    public List<BookingDtoWithBookerAndItem> findAllById(long userId, BookingState state) {
        User user = getUser(userId);               //проверка на существование пользователя
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED.toString());
        }
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(b -> BookingMapper.toBookingDtoWithBookerAndItem(b, getItem(b.getItem()), getUser(b.getBooker())))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoWithBookerAndItem> findAllByOwner(long userId, BookingState state) {
        User user = getUser(userId);               //проверка на существование пользователя
        List<Item> items = itemService.findAllById(userId).stream().map(ItemMapper::toItem).collect(Collectors.toList());
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> itemsId = items.stream().map(i->i.getId()).collect(Collectors.toSet());

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemInOrderByStartDesc(itemsId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemInAndEndBeforeOrderByStartDesc(itemsId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemInAndStartAfterOrderByStartDesc(itemsId, now);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(itemsId, now, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemsId, BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemsId, BookingStatus.REJECTED.toString());
        }
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(b -> BookingMapper.toBookingDtoWithBookerAndItem(b, getItem(b.getItem()), getUser(b.getBooker())))
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return UserMapper.toUser(userService.findById(id));
    }

    private Item getItem(Long id) {
        return ItemMapper.toItem(itemService.findById(id));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }
}
