package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Не указано имя пользователя.")
    private String name;
    @NotNull(message = "Не указан адрес электронной почты.")
    @Email(message = "Указан некорректный адрес электронной почты.")
    private String email;
}
