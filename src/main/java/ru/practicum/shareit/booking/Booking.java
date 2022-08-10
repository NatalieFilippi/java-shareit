package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private long id;            //уникальный идентификатор бронирования;
    private LocalDateTime start;//дата и время начала бронирования;
    private LocalDateTime end;  //дата и время конца бронирования;
    private long item;          //вещь, которую пользователь бронирует;
    private long booker;        //пользователь, который осуществляет бронирование;
    private BookingStatus status;       //статус бронирования.
}