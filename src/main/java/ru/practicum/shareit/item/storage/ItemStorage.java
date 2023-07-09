package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Optional<Item> createItem(Item item);

    Optional<Item> getItemById(long id);

    List<Item> getAllItems(long userId);

    Item updateItem(long userId, Item item);

    void removeItem(Long id);

    List<Item> search(String text);
}
