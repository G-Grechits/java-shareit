package ru.practicum.shareit.item;

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
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен GET-запрос /items: userId={}, from={}, size={}.", userId, from, size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET-запрос /items: itemId={}, userId={}.", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос /items: userId={}, item: {}.", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH-запрос /items: itemId={}, userId={}, item: {}.", itemId, userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен GET-запрос /items: text: {}, from={}, size={}.", text, from, size);
        return itemClient.searchItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен POST-запрос /items: itemId={}, userId={}, comment: {}.", itemId, userId, commentDto);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
