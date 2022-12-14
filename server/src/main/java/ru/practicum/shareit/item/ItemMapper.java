package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;

import java.util.ArrayList;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null, null);
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemDtoWithInfo toItemDtoWithInfo(Item item) {
        return new ItemDtoWithInfo(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                null, null, new ArrayList<>());
    }
}
