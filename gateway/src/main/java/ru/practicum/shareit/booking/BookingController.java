package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.WrongParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookingsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
													  @RequestParam(defaultValue = "ALL") String state,
													  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
													  @Positive @RequestParam(defaultValue = "20") int size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new WrongParameterException("Unknown state: " + state));
		log.info("Получен GET-запрос /bookings: userId={}, state: {}, from={}, size={}.", userId, state, from, size);
		return bookingClient.getBookingsByUserId(userId, bookingState, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
														   @RequestParam(defaultValue = "ALL") String state,
														   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
														   @Positive @RequestParam(defaultValue = "20") int size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new WrongParameterException("Unknown state: " + state));
		log.info("Получен GET-запрос /bookings: userId={}, state: {}, from={}, size={}.", userId, state, from, size);
		return bookingClient.getBookingsByItemOwnerId(userId, bookingState, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
												 @RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Получен GET-запрос /bookings: bookingId={}, userId={}.", bookingId, userId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												@Valid @RequestBody BookingDto bookingDto) {
		log.info("Получен POST-запрос /bookings: userId={}, booking: {}.", userId, bookingDto);
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
												 @RequestHeader("X-Sharer-User-Id") long userId,
												 @RequestParam Boolean approved) {
		log.info("Получен PATCH-запрос /bookings: bookingId={}, userId={}, approved: {}.", bookingId, userId, approved);
		return bookingClient.approveBooking(bookingId, userId, approved);
	}
}
