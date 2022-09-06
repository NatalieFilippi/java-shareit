package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        return Booking.builder()
                .id(bookingDto.getId())
                .booker(bookingDto.getBooker())
                .item(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto (Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDto.builder()
                .id((booking.getId()))
                .booker(booking.getBooker())
                .itemId(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoWithBookerAndItem toBookingDtoWithBookerAndItem(Booking booking, Item item, User booker) {
        if (booking == null) {
            return null;
        }
        return BookingDtoWithBookerAndItem.builder()
                .id((booking.getId()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booker)
                .itemDto(ItemMapper.toItemDto(item))
                .build();
    }
}
