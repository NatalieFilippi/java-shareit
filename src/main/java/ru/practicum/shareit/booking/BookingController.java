package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerAndItem;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithBookerAndItem update(@RequestHeader("X-Sharer-User-Id") long owner,
                                              @RequestParam("approved") boolean approved,
                                              @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.update(owner, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithBookerAndItem findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoWithBookerAndItem> findAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL") BookingState state) throws ObjectNotFoundException {
        return bookingService.findAllById(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoWithBookerAndItem> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(required = false, defaultValue = "ALL") BookingState state) throws ObjectNotFoundException {
        return bookingService.findAllByOwner(userId, state);
    }
}
