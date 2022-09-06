package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            //уникальный идентификатор бронирования;
    @Column(name = "start_date")
    private LocalDateTime start;//дата и время начала бронирования;
    @Column(name = "end_date")
    private LocalDateTime end;  //дата и время конца бронирования;
    @Column(name = "item_id")
    private long item;          //вещь, которую пользователь бронирует;
    @Column(name = "booker_id")
    private long booker;        //пользователь, который осуществляет бронирование;
    @Enumerated(EnumType.ORDINAL)
    private BookingStatus status;       //статус бронирования.
}