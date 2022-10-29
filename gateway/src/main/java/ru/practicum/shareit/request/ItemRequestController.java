package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET-запрос /requests: userId={}.", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                       @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен GET-запрос /requests: userId={}, from={}, size={}.", userId, from, size);
        return itemRequestClient.getOtherItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET-запрос /requests: requestId={}, userId={}.", requestId, userId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос /requests: userId={}, itemRequest: {}.", userId, itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }
}
