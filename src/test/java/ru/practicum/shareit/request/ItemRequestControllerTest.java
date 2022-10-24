package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getItemRequestsByUserId() throws Exception {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        ItemRequestDtoWithInfo itemRequestDtoWithInfo = ItemRequestMapper.toItemRequestDtoWithInfo(itemRequest);
        List<ItemRequestDtoWithInfo> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequestDtoWithInfo);
        itemRequests.add(itemRequestDtoWithInfo);
        when(itemRequestService.getItemRequestsByUserId(userId))
                .thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"description\": \"test request\"}," +
                        " {\"id\": 1,\"description\": \"test request\"}]"));
        verify(itemRequestService, times(1)).getItemRequestsByUserId(userId);
    }

    @Test
    void getOtherItemRequests() throws Exception {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        ItemRequestDtoWithInfo itemRequestDtoWithInfo = ItemRequestMapper.toItemRequestDtoWithInfo(itemRequest);
        List<ItemRequestDtoWithInfo> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequestDtoWithInfo);
        itemRequests.add(itemRequestDtoWithInfo);
        when(itemRequestService.getOtherItemRequests(userId, 0, 20))
                .thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"description\": \"test request\"}," +
                        " {\"id\": 1,\"description\": \"test request\"}]"));
        verify(itemRequestService, times(1)).getOtherItemRequests(userId, 0, 20);
    }

    @Test
    void getItemRequestById() throws Exception {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        ItemRequestDtoWithInfo itemRequestDtoWithInfo = ItemRequestMapper.toItemRequestDtoWithInfo(itemRequest);
        when(itemRequestService.getItemRequestById(itemRequest.getId(), userId))
                .thenReturn(itemRequestDtoWithInfo);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"description\": \"test request\"}"));
        verify(itemRequestService, times(1)).getItemRequestById(itemRequest.getId(), userId);
    }

    @Test
    void createItemRequest() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        when(itemRequestService.createItemRequest(itemRequestDto, userId))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"description\": \"test request\"}"));
        verify(itemRequestService, times(1)).createItemRequest(itemRequestDto, userId);
    }

    ItemRequest getItemRequest() {
        User user = new User(1L, "test user", "testuser@mail.com");
        return new ItemRequest(1L, "test request", user, LocalDateTime.now());
    }
}