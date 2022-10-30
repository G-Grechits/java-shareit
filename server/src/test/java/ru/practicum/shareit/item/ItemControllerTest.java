package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getItemsByUserId() throws Exception {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        ItemDtoWithInfo itemDtoWithInfo = ItemMapper.toItemDtoWithInfo(item);
        List<ItemDtoWithInfo> items = new ArrayList<>();
        items.add(itemDtoWithInfo);
        items.add(itemDtoWithInfo);
        when(itemService.getItemsByUserId(userId, 0, 20))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true," +
                        "\"lastBooking\": null,\"nextBooking\": null,\"comments\": []}," +
                        " {\"id\": 1,\"name\": \"test item\",\"description\": \"test description\",\"available\": true," +
                        "\"lastBooking\": null,\"nextBooking\": null,\"comments\": []}]"));
        verify(itemService, times(1)).getItemsByUserId(userId, 0, 20);
    }

    @Test
    void getItemById() throws Exception {
        Item item = getItem();
        Long itemId = item.getId();
        Long userId = item.getOwner().getId();
        ItemDtoWithInfo itemDtoWithInfo = ItemMapper.toItemDtoWithInfo(item);
        when(itemService.getItemById(itemId, userId))
                .thenReturn(itemDtoWithInfo);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\", \"available\": true," +
                        "\"lastBooking\": null,\"nextBooking\": null, \"comments\": []}"));
        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @Test
    void createItem() throws Exception {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.createItem(itemDto, userId))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true,\"requestId\": 1}"));
        verify(itemService, times(1)).createItem(itemDto, userId);
    }

    @Test
    void updateItem() throws Exception {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setName("new item");
        itemDto.setDescription("new description");
        when(itemService.updateItem(itemDto, itemDto.getId(), userId))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\": \"new item\"," +
                        "\"description\": \"new description\",\"available\": true,\"requestId\": 1}"));
        verify(itemService, times(1)).updateItem(itemDto, itemDto.getId(), userId);
    }

    @Test
    void searchItemsByText() throws Exception {
        Item item = getItem();
        String text = item.getDescription().substring(5, 10);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);
        when(itemService.searchItemsByText(text, 0, 20))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\": \"test item\"," +
                        "\"description\": \"test description\",\"available\": true,\"requestId\": 1}]"));
        verify(itemService, times(1)).searchItemsByText(text, 0, 20);
    }

    @Test
    void createComment() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        Item item = getItem();
        CommentDto commentDto = getCommentDto();
        when(itemService.createComment(commentDto, item.getId(), 1))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"text\": \"test comment\"," +
                        "\"authorName\": \"test author\"}"));
        verify(itemService, times(1)).createComment(commentDto, item.getId(), 1);
    }

    private Item getItem() {
        User user1 = new User(1L, "test user1", "testuser1@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "test request", user1, LocalDateTime.now());
        User user2 = new User(2L, "test user2", "testuser2@mail.com");
        return new Item(1L, "test item", "test description", true, user2, itemRequest);
    }

    private CommentDto getCommentDto() {
        return new CommentDto(1L, "test comment", "test author", LocalDateTime.now());
    }
}