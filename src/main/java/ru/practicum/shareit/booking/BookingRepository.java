package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(long id); //все бронирования пользователя

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end); //все завершенные бронирования

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long id, BookingStatus status); //все бронирования в статусе ожидания подтверждения

    List<Booking> findAllByItem_IdInOrderByStartDesc(Set<Long> id);

    List<Booking> findAllByItem_IdInAndEndBeforeOrderByStartDesc(Set<Long> id, LocalDateTime end);

    List<Booking> findAllByItem_IdInAndStartAfterOrderByStartDesc(Set<Long> id, LocalDateTime start); //все будущие бронирования

    List<Booking> findAllByItem_IdInAndStartBeforeAndEndAfterOrderByStartDesc(Set<Long> id, LocalDateTime start, LocalDateTime end); //все текущие бронирования

    List<Booking> findAllByItem_IdInAndStatusOrderByStartDesc(Set<Long> id, BookingStatus status); //все бронирования в статусе ожидания подтверждения

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = ?1 and b.status = ?2 order by b.start asc")
    List<BookingForItemDto> findAllByItem_IdAndStatusOrderByStartAsc(Long id, BookingStatus status);
}
