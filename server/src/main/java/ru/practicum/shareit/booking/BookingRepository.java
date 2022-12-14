package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBooker_Id(long id, Pageable pageable); //все бронирования пользователя

    Page<Booking> findAllByBooker_IdAndEndBefore(Long id, LocalDateTime end, Pageable pageable); //все завершенные бронирования

    Page<Booking> findAllByBooker_IdAndStartAfter(Long id, LocalDateTime start, Pageable pageable); //все будущие бронирования

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable); //все текущие бронирования

    Page<Booking> findAllByBooker_IdAndStatus(Long id, BookingStatus status, Pageable pageable); //все бронирования в статусе ожидания подтверждения


    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = ?1 order by b.start desc")
    Page<Booking> findAllByItem_IdIn(Long id, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = ?1 " +
            "and b.end < ?2 order by b.start desc")
    Page<Booking> findAllByItem_IdInAndEndBefore(Long id, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = ?1 " +
            "and b.start > ?2 order by b.start desc")
    Page<Booking> findAllByItem_IdInAndStartAfter(Long id, LocalDateTime start, Pageable pageable); //все будущие бронирования

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = ?1 " +
            "and b.start < ?2 and b.end > ?3 order by b.start desc")
    Page<Booking> findAllByItem_IdInAndStartBeforeAndEndAfter(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable); //все текущие бронирования

    @Query("select b from Booking b join Item i on b.item.id = i.id where i.owner = ?1 " +
            "and b.status = ?2 order by b.start desc")
    Page<Booking> findAllByItem_IdInAndStatus(Long id, BookingStatus status, Pageable pageable); //все бронирования в статусе ожидания подтверждения

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, u.id) from Booking b" +
            " join b.booker u where b.item.id = ?1 and b.status = ?2 order by b.start asc")
    List<BookingForItemDto> findAllByItem_IdAndStatus(Long id, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime end);

}
