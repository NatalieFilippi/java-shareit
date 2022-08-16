package ru.practicum.shareit.item.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private long id;            //уникальный идентификатор вещи;
    @NotBlank(message = "Не указано наименование вещи для аренды")
    private String name;        //краткое название;
    @NotBlank(message = "Не указано описание вещи для аренды")
    private String description; //развёрнутое описание;
    @NotNull
    private Boolean available;  //статус о том, доступна или нет вещь для аренды;
    private long owner;         //владелец вещи;
    private ItemRequest request;//если вещь была создана по запросу другого пользователя,
                                //то в этом поле будет храниться ссылка на соответствующий запрос
}
