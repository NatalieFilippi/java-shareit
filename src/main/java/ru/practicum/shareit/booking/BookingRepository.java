package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc(long id); //все бронирования пользователя
    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end); //все завершенные бронирования
    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(Long id, LocalDateTime start); //все будущие бронирования
    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end); //все текущие бронирования
    List<Booking> findAllByBookerAndStatusOrderByStartDesc(Long id, String status); //все бронирования в статусе ожидания подтверждения


    List<Booking> findAllByItemInOrderByStartDesc(Set<Long> id);
    List<Booking> findAllByItemInAndEndBeforeOrderByStartDesc(Set<Long> id, LocalDateTime end);
    List<Booking> findAllByItemInAndStartAfterOrderByStartDesc(Set<Long> id, LocalDateTime start); //все будущие бронирования
    List<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(Set<Long> id, LocalDateTime start, LocalDateTime end); //все текущие бронирования
    List<Booking> findAllByItemInAndStatusOrderByStartDesc(Set<Long> id, String status); //все бронирования в статусе ожидания подтверждения

}
