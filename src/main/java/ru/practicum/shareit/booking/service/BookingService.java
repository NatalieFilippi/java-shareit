package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDtoWithBookerAndItem update(Long owner, boolean approved, Long bookingId);

    BookingDtoWithBookerAndItem findById(Long userId, Long bookingId);

    List<BookingDtoWithBookerAndItem> findAllById(long userId, BookingState state);

    List<BookingDtoWithBookerAndItem> findAllByOwner(long userId, BookingState state);
}
