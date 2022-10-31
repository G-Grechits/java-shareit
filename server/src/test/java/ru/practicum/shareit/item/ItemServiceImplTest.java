package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {
    private ItemServiceImpl itemService;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private UserService userService;


    @BeforeEach
    void initialize() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        userService = mock(UserService.class);
        itemService = new ItemServiceImpl(itemRepository, itemRequestRepository, bookingRepository, commentRepository,
                userService);
    }

    @Test
    void getItemsByUserId() {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        User requester = getRequester(item);
        Comment comment = getComment(item, requester);
        Booking booking = getBooking(item, requester);
        Pageable pageable = PageRequest.of(0, 20);
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(item.getOwner()));
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(Collections.singletonList(comment));
        when(itemRepository.findAllByOwnerId(userId, pageable))
                .thenReturn(Collections.singletonList(item));
        List<ItemDtoWithInfo> items = itemService.getItemsByUserId(userId, 0, 20);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals("test item", items.get(0).getName());
        assertEquals("test description", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
        assertEquals(BookingMapper.toBookingShort(booking), items.get(0).getLastBooking());
        assertNull(items.get(0).getNextBooking());
        assertEquals(CommentMapper.toCommentDto(comment), items.get(0).getComments().get(0));
        verify(itemRepository, times(1)).findAllByOwnerId(userId, pageable);
    }

    @Test
    void getItemById() {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        Long itemId = item.getId();
        Long wrongId = itemId + 1;
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(item.getOwner()));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.findById(wrongId))
                .thenThrow(new ObjectNotFoundException(String.format("Вещь с ID = %d не найдена.", wrongId)));
        ItemDtoWithInfo itemDtoWithInfo = itemService.getItemById(itemId, userId);

        Throwable throwable = assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(wrongId, userId));
        assertNotNull(throwable);
        assertEquals("Вещь с ID = 2 не найдена.", throwable.getMessage());
        assertNotNull(itemDtoWithInfo);
        assertEquals(1, itemDtoWithInfo.getId());
        assertEquals("test item", itemDtoWithInfo.getName());
        assertEquals("test description", itemDtoWithInfo.getDescription());
        assertEquals(true, itemDtoWithInfo.getAvailable());
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void createItem() {
        Item item = getItem();
        Long userId = item.getOwner().getId();
        Long itemRequestId = item.getRequest().getId();
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(item.getOwner()));
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(item.getRequest()));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userId);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(itemRequestId, itemDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        Item item1 = getItem();
        Long userId = item1.getOwner().getId();
        Long itemId = item1.getId();
        Item item2 = getItem();
        item2.setName("new item");
        item2.setDescription("new description");
        when(userService.getUserById(userId))
                .thenReturn(UserMapper.toUserDto(item1.getOwner()));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item2);
        ItemDto itemDto = itemService.updateItem(ItemMapper.toItemDto(item2), itemId, userId);

        assertNotNull(itemDto);
        assertEquals(item2.getId(), itemDto.getId());
        assertEquals(item2.getName(), itemDto.getName());
        assertEquals(item2.getDescription(), itemDto.getDescription());
        assertEquals(item2.getAvailable(), itemDto.getAvailable());
        assertEquals(item2.getRequest().getId(), itemDto.getRequestId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void searchItemsByText() {
        Item item = getItem();
        String text = item.getDescription().substring(5, 10);
        Pageable pageable = PageRequest.of(0, 20);
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text, pageable))
                .thenReturn(items);
        List<ItemDto> foundItems = itemService.searchItemsByText(text, 0, 20);

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals(1, foundItems.get(0).getId());
        assertEquals("test item", foundItems.get(0).getName());
        assertEquals("test description", foundItems.get(0).getDescription());
        assertEquals(true, foundItems.get(0).getAvailable());
        verify(itemRepository, times(1))
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text, pageable);
    }

    @Test
    void createComment() {
        Item item = getItem();
        Long itemId = item.getId();
        User requester = getRequester(item);
        Long bookerId = requester.getId();
        Comment comment = getComment(item, requester);
        Booking booking = getBooking(item, requester);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(userService.getUserById(bookerId))
                .thenReturn(UserMapper.toUserDto(requester));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(eq(bookerId), eq(itemId),
                eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        CommentDto commentDto = itemService.createComment(CommentMapper.toCommentDto(comment), itemId, bookerId);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    private Item getItem() {
        User user1 = new User(1L, "test user1", "testuser1@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "test request", user1, LocalDateTime.now());
        User user2 = new User(2L, "test user2", "testuser2@mail.com");
        return new Item(1L, "test item", "test description", true, user2, itemRequest);
    }

    private User getRequester(Item item) {
        return item.getRequest().getRequester();
    }

    private Comment getComment(Item item, User user) {
        return new Comment(1L, "test comment", item, user, LocalDateTime.now());
    }

    private Booking getBooking(Item item, User user) {
        return new Booking(1L, LocalDateTime.now().minusWeeks(1), LocalDateTime.now().minusDays(5), item, user,
                Status.APPROVED);
    }
}