package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ObjectNotFoundException;

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
                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequestDto update(@RequestHeader("X-Sharer-User-Id") long owner,
                                    @RequestParam("approved") boolean approved,
                                    @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.update(owner, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingRequestDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable("bookingId") long bookingId) throws ObjectNotFoundException {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingRequestDto> findAllById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                               @RequestParam(required = false, defaultValue = "0") int from,
                                               @RequestParam(required = false, defaultValue = "20") int size) throws ObjectNotFoundException {
        return bookingService.findAllById(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                                  @RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "20") int size) throws ObjectNotFoundException {
        return bookingService.findAllByOwner(userId, state, from, size);
    }
}
