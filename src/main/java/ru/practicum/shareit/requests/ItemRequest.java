package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private long id;                //уникальный идентификатор запроса;
    private String description;     //текст запроса, содержащий описание требуемой вещи;
    private long requestor;         //пользователь, создавший запрос;
    private LocalDateTime created;  //дата и время создания запроса.
}