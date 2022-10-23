package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(1L, "test user1", "testuser1@mail.com"));
        item = itemRepository.save(new Item(1L, "test item", "test description", true,
                owner, null));
        booker = userRepository.save(new User(2L, "test user2", "testuser2@mail.com"));
        booking = bookingRepository.save(new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, Status.APPROVED));
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId(), Pageable.unpaged());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        booking.setStart(booking.getStart().minusWeeks(1));
        booking.setEnd(booking.getEnd().minusWeeks(1));
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertSame(booking, bookings.get(0));
        assertTrue(bookings.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void isAvailableForBooking() {
        boolean isAvailable1 = bookingRepository.isAvailableForBooking(item.getId(),
                LocalDateTime.now().plusDays(1).plusHours(1), LocalDateTime.now().plusDays(2).minusHours(1));
        boolean isAvailable2 = bookingRepository.isAvailableForBooking(item.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(6));

        assertTrue(isAvailable1);
        assertFalse(isAvailable2);
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusAndEndBefore() {
        booking.setStart(booking.getStart().minusWeeks(1));
        booking.setEnd(booking.getEnd().minusWeeks(1));
        List<Booking> approvedBookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(booker.getId(),
                item.getId(), Status.APPROVED, LocalDateTime.now());
        List<Booking> rejectedBookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(booker.getId(),
                item.getId(), Status.REJECTED, LocalDateTime.now());

        assertEquals(1, approvedBookings.size());
        assertSame(booking, approvedBookings.get(0));
        assertTrue(approvedBookings.get(0).getEnd().isBefore(LocalDateTime.now()));
        assertEquals(0, rejectedBookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfter() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusWeeks(1));
        List<Booking> currentBookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), Pageable.unpaged());
        List<Booking> futureBookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                LocalDateTime.now().plusWeeks(2), LocalDateTime.now().plusWeeks(3), Pageable.unpaged());

        assertEquals(1, currentBookings.size());
        assertSame(booking, currentBookings.get(0));
        assertEquals(0, futureBookings.size());
    }
}