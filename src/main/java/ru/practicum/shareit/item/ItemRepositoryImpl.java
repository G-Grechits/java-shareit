package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> getItemById(long id) {
        Item item = items.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
        return Optional.ofNullable(item);
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getItemId());
        items.compute(item.getOwner().getId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.get(item.getOwner().getId()).removeIf(i -> i.getId().equals(item.getId()));
        items.get(item.getOwner().getId()).add(item);
        return item;
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        String loweredText = text.toLowerCase();
        return items.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(loweredText)
                        || i.getDescription().toLowerCase().contains(loweredText))
                .collect(Collectors.toList());
    }

    private long getItemId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
