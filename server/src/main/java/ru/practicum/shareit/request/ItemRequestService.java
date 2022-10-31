package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDtoWithInfo> getItemRequestsByUserId(long userId);

    List<ItemRequestDtoWithInfo> getOtherItemRequests(long userId, int from, int size);

    ItemRequestDtoWithInfo getItemRequestById(long id, long userId);

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, long userId);
}
