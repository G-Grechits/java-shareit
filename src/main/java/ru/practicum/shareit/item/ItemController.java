package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Create;
import ru.practicum.shareit.marker.Update;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> items = itemService.getItemsByUserId(userId);
        log.info("Получен список всех вещей пользователя.");
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        ItemDto itemDto = itemService.getItemById(itemId);
        log.info("Получена вещь {}.", itemDto.getName());
        return itemDto;
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
    public List<ItemDto> searchItemsByText(@RequestParam(required = false) String text) {
        List<ItemDto> items = itemService.searchItemsByText(text);
        log.info("Получен список всех вещей, содержащих текст '{}'.", text);
        return items;
    }
}
