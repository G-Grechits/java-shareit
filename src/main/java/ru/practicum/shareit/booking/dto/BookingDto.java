package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(groups = {Create.class}, message = "Не указаны дата и время начала бронирования.")
    @FutureOrPresent(groups = {Create.class}, message = "Указаны некорректные дата и время начала бронирования.")
    private LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "Не указаны дата и время окончания бронирования.")
    @FutureOrPresent(groups = {Create.class}, message = "Указаны некорректные дата и время окончания бронирования.")
    private LocalDateTime end;
    @NotNull(groups = {Create.class}, message = "Не указан ID арендуемой вещи.")
    private Long itemId;
}
