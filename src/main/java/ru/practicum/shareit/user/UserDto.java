package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Не указано имя пользователя.")
    private String name;
    @NotNull(groups = {Create.class}, message = "Не указан адрес электронной почты.")
    @Email(groups = {Create.class}, message = "Указан некорректный адрес электронной почты.")
    private String email;
}
