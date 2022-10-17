package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithInfo;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithInfo> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "20") int size) {
        List<ItemDtoWithInfo> items = itemService.getItemsByUserId(userId, from, size);
        log.info("Получен список всех вещей пользователя.");
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithInfo getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDtoWithInfo item = itemService.getItemById(itemId, userId);
        log.info("Получена вещь {}.", item.getName());
        return item;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto createdItem = itemService.createItem(itemDto, userId);
        log.info("Вещь {} добавлена.", createdItem.getName());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(itemDto, itemId, userId);
        log.info("Данные вещи {} обновлены.", updatedItem.getName());
        return updatedItem;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam(required = false) String text,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "20") int size) {
        List<ItemDto> items = itemService.searchItemsByText(text, from, size);
        log.info("Получен список всех вещей, содержащих текст '{}'.", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                    @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        CommentDto createdComment = itemService.createComment(commentDto, itemId, userId);
        log.info("Добавлен комментарий '{}'.", createdComment.getText());
        return createdComment;
    }
}
