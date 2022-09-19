package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUserId(long userId);

    ItemDto getItemById(long id);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    List<ItemDto> searchItemsByText(String text);
}
