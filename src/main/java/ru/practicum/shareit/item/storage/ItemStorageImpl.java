package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private Long id = 1L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Map<Long, Item>> itemsWithUsers = new HashMap<>();

    @Override
    public Optional<Item> createItem(Item item) {
        item.setId(getNewId());
        final Long ownerId = item.getOwner().getId();
        final Long itemId = item.getId();

        items.put(itemId, item);
        itemsWithUsers.computeIfAbsent(ownerId, k -> new HashMap<>()).put(itemId, item);

        return getItemById(itemId);
    }

    @Override
    public Optional<Item> getItemById(long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id).toBuilder().build());
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getAllItems(long userId) {
        if (itemsWithUsers.containsKey(userId)) {
            return List.copyOf(itemsWithUsers.get(userId).values());
        } else return Collections.emptyList();
    }

    @Override
    public Item updateItem(long userId, Item item) {
        final Long ownerId = items.get(item.getId()).getOwner().getId();
        final Long itemId = item.getId();

        if (!ownerId.equals(userId)) {
            throw new NotFoundException("Вещь не найдена");
        }

        items.put(itemId, item);
        itemsWithUsers.get(ownerId).put(itemId, item);

        return getItemById(itemId).orElseThrow();
    }

    @Override
    public void removeItem(Long id) {
        Item item = getItemById(id).get();
        itemsWithUsers.remove(item.getOwner());
        items.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        String lowerCaseText = text.toLowerCase();

        return items.values()
                .stream()
                .filter(t -> (t.getName().toLowerCase().contains(lowerCaseText)
                        || t.getDescription().toLowerCase().contains(lowerCaseText))
                        && t.getAvailable())
                .collect(Collectors.toList());
    }

    private Long getNewId() {
        return id++;
    }
}
