package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getItemsByUserId(long userId);

    Optional<Item> getItemById(long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    List<Item> searchItemsByText(String text);
}
