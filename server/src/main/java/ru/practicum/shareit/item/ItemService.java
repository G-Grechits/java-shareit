package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithInfo> getItemsByUserId(long userId, int from, int size);

    ItemDtoWithInfo getItemById(long id, long userId);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    List<ItemDto> searchItemsByText(String text, int from, int size);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}
