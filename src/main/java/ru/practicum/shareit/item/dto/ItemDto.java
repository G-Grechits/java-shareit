package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Не указано название вещи.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Не указано описание вещи.")
    private String description;
    @NotNull(groups = {Create.class}, message = "Не указано, доступна ли вещь для аренды.")
    private Boolean available;
    private Long requestId;
}
