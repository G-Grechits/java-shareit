package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserService userService;

    @BeforeEach
    void initialize() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userService);
    }

    @Test
    void getBookingsByUserId() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerId(bookerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "ALL", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        verify(bookingRepository, times(1)).findAllByBookerId(bookerId, pageable);
    }

    @Test
    void getBookingsByUserIdWithStateIsPast() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        booking.setStart(booking.getStart().minusMonths(1));
        booking.setEnd(booking.getEnd().minusMonths(1));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "PAST", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getEnd().isBefore(LocalDateTime.now()));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByUserIdWithStateIsCurrent() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        booking.setStart(booking.getStart().minusMonths(1));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(eq(bookerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "CURRENT", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookings.get(0).getEnd().isAfter(LocalDateTime.now()));
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartBeforeAndEndAfter(
                eq(bookerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByUserIdWithStateIsFuture() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "FUTURE", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByUserIdWithStateIsWaiting() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        booking.setStatus(Status.WAITING);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "WAITING", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(bookerId, Status.WAITING, pageable);
    }

    @Test
    void getBookingsByUserIdWithStateIsRejected() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        booking.setStatus(Status.REJECTED);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService.getBookingsByUserId(bookerId, "REJECTED", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(bookerId, Status.REJECTED, pageable);
    }

    @Test
    void getBookingsByUserIdWithUnknownState() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findAllByBookerIdAndStatus(eq(bookerId), any(Status.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));

        Throwable throwable = assertThrows(WrongParameterException.class,
                () -> bookingService.getBookingsByUserId(bookerId, "UNKNOWN", 0, 20));
        assertNotNull(throwable);
        assertEquals("Unknown state: UNKNOWN", throwable.getMessage());
    }

    @Test
    void getBookingsByItemOwnerId() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "ALL", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        verify(bookingRepository, times(2)).findAllByItemOwnerId(itemOwnerId, pageable);
    }

    @Test
    void getBookingsByItemOwnerIdWithStateIsPast() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        booking.setStart(booking.getStart().minusMonths(1));
        booking.setEnd(booking.getEnd().minusMonths(1));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(itemOwnerId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "PAST", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getEnd().isBefore(LocalDateTime.now()));
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBefore(eq(itemOwnerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByItemOwnerIdWithStateIsCurrent() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        booking.setStart(booking.getStart().minusMonths(1));
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(eq(itemOwnerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "CURRENT", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookings.get(0).getEnd().isAfter(LocalDateTime.now()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                eq(itemOwnerId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByItemOwnerIdWithStateIsFuture() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(itemOwnerId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "FUTURE", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertTrue(bookings.get(0).getStart().isAfter(LocalDateTime.now()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartAfter(
                eq(itemOwnerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getBookingsByItemOwnerIdWithStateIsWaiting() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        booking.setStatus(Status.WAITING);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, Status.WAITING, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "WAITING", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(itemOwnerId, Status.WAITING, pageable);
    }

    @Test
    void getBookingsByItemOwnerIdWithStateIsRejected() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        booking.setStatus(Status.REJECTED);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, Status.REJECTED, pageable))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoWithInfo> bookings = bookingService
                .getBookingsByItemOwnerId(itemOwnerId, "REJECTED", 0, 20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
        assertEquals("test item", bookings.get(0).getItem().getName());
        assertEquals("test user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(itemOwnerId, Status.REJECTED, pageable);
    }

    @Test
    void getBookingsByItemOwnerIdWithUnknownState() {
        Booking booking = getBooking();
        User itemOwner = booking.getItem().getOwner();
        Long itemOwnerId = itemOwner.getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("start").descending());
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(itemOwner));
        when(bookingRepository.findAllByItemOwnerId(itemOwnerId, pageable))
                .thenReturn(Collections.singletonList(booking));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(eq(itemOwnerId), any(Status.class), eq(pageable)))
                .thenReturn(Collections.singletonList(booking));

        Throwable throwable = assertThrows(WrongParameterException.class,
                () -> bookingService.getBookingsByItemOwnerId(itemOwnerId, "UNKNOWN", 0, 20));
        assertNotNull(throwable);
        assertEquals("Unknown state: UNKNOWN", throwable.getMessage());
    }

    @Test
    void getBookingById() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        Long bookingId = booking.getId();
        Long wrongId = bookingId + 1;
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.findById(wrongId))
                .thenThrow(new ObjectNotFoundException(String.format("Бронирование с ID = %d не найдено.", wrongId)));
        BookingDtoWithInfo bookingDtoWithInfo = bookingService.getBookingById(bookingId, bookerId);

        Throwable throwable = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(wrongId, bookerId));
        assertNotNull(throwable);
        assertEquals("Бронирование с ID = 2 не найдено.", throwable.getMessage());
        assertNotNull(bookingDtoWithInfo);
        assertEquals(1, bookingDtoWithInfo.getId());
        assertEquals("test item", bookingDtoWithInfo.getItem().getName());
        assertEquals("test user2", bookingDtoWithInfo.getBooker().getName());
        assertEquals(Status.APPROVED, bookingDtoWithInfo.getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByIdWithUserIsOutsider() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        long outsiderId = bookerId + booking.getItem().getOwner().getId();
        Long bookingId = booking.getId();
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        Throwable throwable = assertThrows(AccessDeniedException.class,
                () -> bookingService.getBookingById(bookingId, outsiderId));
        assertNotNull(throwable);
        assertEquals("Пользователь с ID = 3 не является владельцем вещи или автором бронирования.",
                throwable.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void createBooking() {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        Long itemId = booking.getItem().getId();
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(booking.getBooker()));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(booking.getItem()));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDtoWithInfo bookingDtoWithInfo = bookingService.createBooking(getBookingDto(booking), bookerId);

        assertNotNull(bookingDtoWithInfo);
        assertEquals(booking.getId(), bookingDtoWithInfo.getId());
        assertEquals(booking.getStart(), bookingDtoWithInfo.getStart());
        assertEquals(booking.getEnd(), bookingDtoWithInfo.getEnd());
        assertEquals(itemId, bookingDtoWithInfo.getItem().getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking() {
        Booking booking = getBooking();
        booking.setStatus(Status.WAITING);
        Long bookingId = booking.getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        when(userService.getUserById(itemOwnerId))
                .thenReturn(UserMapper.toUserDto(booking.getItem().getOwner()));
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDtoWithInfo bookingDtoWithInfo = bookingService.approveBooking(bookingId, itemOwnerId, true);

        assertNotNull(bookingDtoWithInfo);
        assertEquals(booking.getStatus(), bookingDtoWithInfo.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    private Booking getBooking() {
        User owner = new User(1L, "test user1", "testuser1@mail.com");
        Item item = new Item(1L, "test item", "test description", true, owner, null);
        User booker = new User(2L, "test user2", "testuser2@mail.com");
        return new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker,
                Status.APPROVED);
    }

    private BookingDto getBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId());
    }
}