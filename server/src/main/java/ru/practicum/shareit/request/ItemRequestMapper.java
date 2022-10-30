package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), null,
                LocalDateTime.now());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequestDtoWithInfo toItemRequestDtoWithInfo(ItemRequest itemRequest) {
        return new ItemRequestDtoWithInfo(itemRequest.getId(), itemRequest.getDescription(), new ArrayList<>(),
                itemRequest.getCreated());
    }
}
