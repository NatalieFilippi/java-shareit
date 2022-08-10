package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.ItemRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private long id;            //уникальный идентификатор вещи;
    private String name;        //краткое название;
    private String description; //развёрнутое описание;
    private boolean isAvailable;  //статус о том, доступна или нет вещь для аренды;
    private long owner;         //владелец вещи;
    private ItemRequest request;//если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
}
