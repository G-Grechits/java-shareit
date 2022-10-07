package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;
import ru.practicum.shareit.booking.dto.BookingShort;

public class BookingMapper {

    public static Booking toBookingFromDto(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), null, null,
                Status.WAITING);
    }

    public static Booking toBookingFromInfo(BookingDtoWithInfo bookingDtoWithInfo) {
        return new Booking(bookingDtoWithInfo.getId(), bookingDtoWithInfo.getStart(), bookingDtoWithInfo.getEnd(),
                bookingDtoWithInfo.getItem(), bookingDtoWithInfo.getBooker(), bookingDtoWithInfo.getStatus());
    }

    public static BookingDtoWithInfo toBookingDtoWithInfo(Booking booking) {
        return new BookingDtoWithInfo(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(),
                booking.getBooker(), booking.getStatus());
    }

    public static BookingShort toBookingShort(Booking booking) {
        return new BookingShort(booking.getId(), booking.getBooker() != null ? booking.getBooker().getId() : null);
    }
}
