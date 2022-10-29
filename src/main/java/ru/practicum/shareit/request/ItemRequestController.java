package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithInfo;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDtoWithInfo> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestService.getItemRequestsByUserId(userId);
        log.info("Получен список всех запросов пользователя.");
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithInfo> getOtherItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                             @Positive @RequestParam(defaultValue = "20") int size) {
        List<ItemRequestDtoWithInfo> itemRequests = itemRequestService.getOtherItemRequests(userId, from, size);
        log.info("Получен список запросов, созданных другими пользователями.");
        return itemRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithInfo getItemRequestById(@PathVariable long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemRequestDtoWithInfo itemRequest = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Получен запрос с ID = {}.", requestId);
        return itemRequest;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto createdItemRequest = itemRequestService.createItemRequest(itemRequestDto, userId);
        log.info("Добавлен запрос '{}'.", createdItemRequest.getDescription());
        return createdItemRequest;
    }
}
