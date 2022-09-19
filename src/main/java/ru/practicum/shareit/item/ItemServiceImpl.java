package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        userService.getUserById(userId);
        List<Item> items = itemRepository.getItemsByUserId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemRepository.getItemById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Вещь с ID = %d не найдена.", id)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        UserDto user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, UserMapper.toUser(user));
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        UserDto user = userService.getUserById(userId);
        ItemDto formerItem = getItemById(id);
        if (formerItem.getOwnerId() != userId) {
            throw new AccessDeniedException(String.format("Пользователь с ID = %d не является владельцем вещи.", userId));
        }
        if (itemDto.getName() != null) {
            formerItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            formerItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            formerItem.setAvailable(itemDto.getAvailable());
        }
        Item item = ItemMapper.toItem(formerItem, UserMapper.toUser(user));
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchItemsByText(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
