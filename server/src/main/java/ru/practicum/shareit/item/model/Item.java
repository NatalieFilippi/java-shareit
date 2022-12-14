package ru.practicum.shareit.item.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;            //уникальный идентификатор вещи;
    @Column
    private String name;        //краткое название;
    @Column
    private String description; //развёрнутое описание;
    @JsonProperty("available")
    @Column(name = "is_available")
    private boolean isAvailable;  //статус о том, доступна или нет вещь для аренды;
    @Column(name = "owner_id")
    private long owner;         //владелец вещи;
    @Column(name = "request_id")
    private long request;//если вещь была создана по запросу другого пользователя,
    //то в этом поле будет храниться ссылка на соответствующий запрос

}
