package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;

import java.util.List;

public interface BookingService {

    List<BookingDtoWithInfo> getBookingsByUserId(long userId, String state);

    List<BookingDtoWithInfo> getBookingsByItemOwnerId(long itemOwnerId, String state);

    BookingDtoWithInfo getBookingById(long id, long userId);

    BookingDtoWithInfo createBooking(BookingDto bookingDto, long userId);

    BookingDtoWithInfo approveBooking(long id, long userId, Boolean approve);
}
