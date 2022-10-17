package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;
import ru.practicum.shareit.marker.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDtoWithInfo> getBookingsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                        @Positive @RequestParam(defaultValue = "20") int size) {
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(userId, state, from, size);
        log.info("Получен список всех бронирований пользователя с ID = {}.", userId);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDtoWithInfo> getBookingsByItemOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                             @Positive @RequestParam(defaultValue = "20") int size) {
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByItemOwnerId(userId, state, from, size);
        log.info("Получен список бронирований всех вещей пользователя с ID = {}.", userId);
        return bookings;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithInfo getBookingById(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingDtoWithInfo booking = bookingService.getBookingById(bookingId, userId);
        log.info("Получено бронирование с ID = {}.", bookingId);
        return booking;
    }

    @PostMapping
    public BookingDtoWithInfo createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        BookingDtoWithInfo createdBooking = bookingService.createBooking(bookingDto, userId);
        log.info("Бронирование на вещь {} создано.", createdBooking.getItem().getName());
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithInfo approveBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam Boolean approved) {
        BookingDtoWithInfo approvedBooking = bookingService.approveBooking(bookingId, userId, approved);
        log.info("Получен ответ на запрос на бронирование с ID = {}.", bookingId);
        return approvedBooking;
    }
}
