package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String name;
    @NotBlank(message = "Не указан email пользователя!")
    @Email(message = "Не валидный email пользователя!")
    private String email;

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }
}