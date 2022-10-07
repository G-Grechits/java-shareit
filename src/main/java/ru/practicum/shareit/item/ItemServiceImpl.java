package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.WrongParameterException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<ItemDtoWithInfo> getItemsByUserId(long userId) {
        userService.getUserById(userId);
        List<ItemDtoWithInfo> items = itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoWithInfo)
                .collect(Collectors.toList());
        for (ItemDtoWithInfo item : items) {
            setBookingsForItem(item);
            List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
            if (!comments.isEmpty()) {
                item.setComments(comments);
            }
        }
        items.sort(Comparator.comparing(ItemDtoWithInfo::getId));
        return items;
    }

    @Override
    public ItemDtoWithInfo getItemById(long id, long userId) {
        userService.getUserById(userId);
        Item item = getItemFromRepositoryById(id);
        ItemDtoWithInfo itemDtoWithInfo = ItemMapper.toItemDtoWithInfo(item);
        if (item.getOwner().getId() == userId) {
            setBookingsForItem(itemDtoWithInfo);
        }
        List<CommentDto> comments = commentRepository.findAllByItemId(id).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (!comments.isEmpty()) {
            itemDtoWithInfo.setComments(comments);
        }
        return itemDtoWithInfo;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        UserDto user = userService.getUserById(userId);
        Item item = ItemMapper.toItemFromDto(itemDto);
        item.setOwner(UserMapper.toUser(user));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        userService.getUserById(userId);
        Item formerItem = getItemFromRepositoryById(id);
        if (formerItem.getOwner().getId() != userId) {
            throw new AccessDeniedException(String.format("Пользователь с ID = %d не является владельцем вещи.", userId));
        }
        formerItem.setName(itemDto.getName() != null ? itemDto.getName() : formerItem.getName());
        formerItem.setDescription(
                itemDto.getDescription() != null ? itemDto.getDescription() : formerItem.getDescription());
        formerItem.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : formerItem.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(formerItem));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text);
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        Item item = getItemFromRepositoryById(itemId);
        User user = UserMapper.toUser(userService.getUserById(userId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).stream()
                .noneMatch(b -> b.getStatus().equals(Status.APPROVED))) {
            throw new WrongParameterException(String.format("Пользователь с ID = %d не арендовал данную вещь.", userId));
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item getItemFromRepositoryById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Вещь с ID = %d не найдена.", id)));
    }

    private void setBookingsForItem(ItemDtoWithInfo itemDtoWithInfo) {
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(
                itemDtoWithInfo.getId(), LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(
                itemDtoWithInfo.getId(), LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            itemDtoWithInfo.setLastBooking(BookingMapper.toBookingShort(lastBookings.get(0)));
        }
        if (!nextBookings.isEmpty()) {
            itemDtoWithInfo.setNextBooking(BookingMapper.toBookingShort(nextBookings.get(0)));
        }
    }
}
