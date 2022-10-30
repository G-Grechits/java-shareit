package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithInfo;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getBookingsByUserId() throws Exception {
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        BookingDtoWithInfo bookingDtoWithInfo = BookingMapper.toBookingDtoWithInfo(booking);
        List<BookingDtoWithInfo> bookings = new ArrayList<>();
        bookings.add(bookingDtoWithInfo);
        bookings.add(bookingDtoWithInfo);
        when(bookingService.getBookingsByUserId(bookerId, "ALL", 0, 20))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}, " +
                        "{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}]"));
        verify(bookingService, times(1)).getBookingsByUserId(bookerId, "ALL", 0, 20);
    }

    @Test
    void getBookingsByItemOwnerId() throws Exception {
        Booking booking = getBooking();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        BookingDtoWithInfo bookingDtoWithInfo = BookingMapper.toBookingDtoWithInfo(booking);
        List<BookingDtoWithInfo> bookings = new ArrayList<>();
        bookings.add(bookingDtoWithInfo);
        bookings.add(bookingDtoWithInfo);
        when(bookingService.getBookingsByItemOwnerId(itemOwnerId, "ALL", 0, 20))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", itemOwnerId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}, " +
                        "{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}]"));
        verify(bookingService, times(1))
                .getBookingsByItemOwnerId(itemOwnerId, "ALL", 0, 20);
    }

    @Test
    void getBookingById() throws Exception {
        Booking booking = getBooking();
        Long bookingId = booking.getId();
        Long bookerId = booking.getBooker().getId();
        BookingDtoWithInfo bookingDtoWithInfo = BookingMapper.toBookingDtoWithInfo(booking);
        when(bookingService.getBookingById(bookingId, bookerId))
                .thenReturn(bookingDtoWithInfo);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}"));
        verify(bookingService, times(1)).getBookingById(bookingId, bookerId);
    }

    @Test
    void createBooking() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        Booking booking = getBooking();
        Long bookerId = booking.getBooker().getId();
        BookingDto bookingDto = getBookingDto(booking);
        BookingDtoWithInfo bookingDtoWithInfo = BookingMapper.toBookingDtoWithInfo(booking);
        when(bookingService.createBooking(bookingDto, bookerId))
                .thenReturn(bookingDtoWithInfo);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}}"));
        verify(bookingService, times(1)).createBooking(bookingDto, bookerId);
    }

    @Test
    void approveBooking() throws Exception {
        Booking booking = getBooking();
        Long bookingId = booking.getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();
        booking.setStatus(Status.WAITING);
        BookingDtoWithInfo bookingDtoWithInfo = BookingMapper.toBookingDtoWithInfo(booking);
        bookingDtoWithInfo.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(bookingId, itemOwnerId, true))
                .thenReturn(bookingDtoWithInfo);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1," +
                        "\"item\": {\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"owner\": {\"id\": 1,\"name\": \"test user1\",\"email\": \"testuser1@mail.com\"}," +
                        "\"request\": null}," +
                        "\"booker\": {\"id\": 2,\"name\": \"test user2\",\"email\": \"testuser2@mail.com\"}," +
                        "\"status\": \"APPROVED\"}"));
        verify(bookingService, times(1)).approveBooking(bookingId, itemOwnerId, true);
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