package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Не указано название вещи.")
    private String name;
    @NotBlank(message = "Не указано описание вещи.")
    private String description;
    @NotNull(message = "Не указано, доступна ли вещь для аренды.")
    private Boolean available;
    private Long requestId;
}
