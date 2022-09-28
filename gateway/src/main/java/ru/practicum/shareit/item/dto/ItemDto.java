package ru.practicum.shareit.item.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String name;        //краткое название;
    private String description; //развёрнутое описание;
    private Boolean available;  //статус о том, доступна или нет вещь для аренды;
    @JsonProperty("requestId")
    private long request;//если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос
}
