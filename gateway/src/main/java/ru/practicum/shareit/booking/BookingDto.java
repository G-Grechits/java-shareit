package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
	private Long itemId;
	@NotNull(message = "Не указаны дата и время начала бронирования.")
	@FutureOrPresent(message = "Указаны некорректные дата и время начала бронирования.")
	private LocalDateTime start;
	@NotNull(message = "Не указаны дата и время окончания бронирования.")
	@Future(message = "Указаны некорректные дата и время окончания бронирования.")
	private LocalDateTime end;
}
