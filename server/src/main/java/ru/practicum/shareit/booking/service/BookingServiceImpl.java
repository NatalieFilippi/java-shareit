package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private static final String NOT_FOUND = "Бронирование не найдено: ";

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User user = getUser(userId);               //проверка на существование пользователя
        Item item = getItem(bookingDto.getItemId()); //проверка на существование вещи
        if (!item.isAvailable()) {
            log.debug("Вещь не доступна для бронирования.");
            throw new ValidationException("Вещь не доступна для бронирования.");
        }
        if (userId == item.getOwner()) {
            log.debug("Владедец не может забронировать свою вещь.");
            throw new ObjectNotFoundException("Владедец не может забронировать свою вещь.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingRequestDto update(Long owner, boolean approved, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
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
        BookingRequestDto bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    @Override
    public BookingRequestDto findById(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (booker.getId() != userId && item.getOwner() != userId) {
            log.debug("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
            throw new ObjectNotFoundException("Просмотр бронирования доступен для владельца вещи или автора бронирования.");
        }
        BookingRequestDto bookingResponse = BookingMapper.toBookingDtoWithBookerAndItem(booking);
        return bookingResponse;
    }

    @Override
    public List<BookingRequestDto> findAllById(long userId, BookingState state, int from, int size) {
        User user = getUser(userId);               //проверка на существование пользователя
        Page<Booking> bookings = Page.empty();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfter(userId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
        }
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithBookerAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingRequestDto> findAllByOwner(long userId, BookingState state, int from, int size) {
        User user = getUser(userId);               //проверка на существование пользователя
        Page<Booking> bookings = Page.empty();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_IdIn(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_IdInAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_IdInAndStartAfter(userId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_IdInAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_IdInAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_IdInAndStatus(userId, BookingStatus.REJECTED, pageable);
        }
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithBookerAndItem)
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return UserMapper.toUser(userService.findById(id));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }

    private Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(NOT_FOUND + id));
    }

}
