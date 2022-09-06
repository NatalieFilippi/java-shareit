package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @NotBlank(message = "Не указан email пользователя!")
    @Email(message = "Не валидный email пользователя!")
    @Column
    private String email;

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
    }
}