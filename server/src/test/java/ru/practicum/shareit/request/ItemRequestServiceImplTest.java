package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private UserService userService;

    @BeforeEach
    void initialize() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        userService = mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userService);
    }

    @Test
    void getItemRequestsByUserId() {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(itemRequest.getRequester()));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.singletonList(itemRequest));
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestService.getItemRequestsByUserId(userId);

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals(1, itemRequests.get(0).getId());
        assertEquals("test request", itemRequests.get(0).getDescription());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    void getOtherItemRequests() {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        Pageable pageable = PageRequest.of(0, 20, Sort.by("created").descending());
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(itemRequest.getRequester()));
        when(itemRequestRepository.findAll(pageable))
                .thenReturn(Page.empty());
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestService.getOtherItemRequests(userId, 0, 20);

        assertNotNull(itemRequests);
        assertEquals(0, itemRequests.size());
        verify(itemRequestRepository, times(1)).findAll(pageable);
    }

    @Test
    void getItemRequestById() {
        ItemRequest itemRequest = getItemRequest();
        Item item = getItem(itemRequest);
        Long userId = itemRequest.getRequester().getId();
        Long itemRequestId = itemRequest.getId();
        Long wrongId = itemRequestId + 1;
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(itemRequest.getRequester()));
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findById(wrongId))
                .thenThrow(new ObjectNotFoundException(String.format("Запрос с ID = %d не найден.", wrongId)));
        when(itemRepository.findAllByRequestId(itemRequestId))
                .thenReturn(Collections.singletonList(item));
        ItemRequestDtoWithInfo itemRequestDtoWithInfo = itemRequestService.getItemRequestById(itemRequestId, userId);

        Throwable throwable = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getItemRequestById(wrongId, userId));
        assertNotNull(throwable);
        assertEquals("Запрос с ID = 2 не найден.", throwable.getMessage());
        assertNotNull(itemRequestDtoWithInfo);
        assertEquals(1, itemRequestDtoWithInfo.getId());
        assertEquals("test request", itemRequestDtoWithInfo.getDescription());
        assertEquals(ItemMapper.toItemDto(item), itemRequestDtoWithInfo.getItems().get(0));
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    void createItemRequest() {
        ItemRequest itemRequest = getItemRequest();
        Long userId = itemRequest.getRequester().getId();
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(itemRequest.getRequester()));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestDto = itemRequestService
                .createItemRequest(ItemRequestMapper.toItemRequestDto(itemRequest), userId);

        assertNotNull(itemRequestDto);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    private ItemRequest getItemRequest() {
        User user = new User(1L, "test user", "testuser@mail.com");
        return new ItemRequest(1L, "test request", user, LocalDateTime.now());
    }

    private Item getItem(ItemRequest itemRequest) {
        User user = new User(2L, "test user2", "testuser2@mail.com");
        return new Item(1L, "test item", "test description", true, user, itemRequest);
    }
}