package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(long userId, CreateUpdateItemDto createUpdateItemDto) {

        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользоваетль не найден."));
        Item item = ItemMapper.toItemFromCreateUpdateItemDto(createUpdateItemDto);
        item.setOwner(user);
        return ItemMapper.toItemDtoFromItem(itemStorage.createItem(item)
                .orElseThrow(() -> new AlreadyExistedException("Такой товар уже существует.")));
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользоваетль не найден."));
        return ItemMapper.toItemDtoFromItem(itemStorage.getItemById(id).orElseThrow(() ->
                new NotFoundException("Такого товара нет.")));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));

        return itemStorage.getAllItems(userId)
                .stream()
                .map(ItemMapper::toItemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, CreateUpdateItemDto createUpdateItemDto) {
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет."));
        Item item = itemStorage.getItemById(itemId).orElseThrow(() -> new NotFoundException("Такого товара нет."));

        if (createUpdateItemDto.getName() != null && !createUpdateItemDto.getName().isBlank()) {
            item.setName(createUpdateItemDto.getName());
        }

        if (createUpdateItemDto.getDescription() != null && !createUpdateItemDto.getDescription().isBlank()) {
            item.setDescription(createUpdateItemDto.getDescription());
        }

        if (createUpdateItemDto.getAvailable() != null) {
            item.setAvailable(createUpdateItemDto.getAvailable());
        }

        return ItemMapper.toItemDtoFromItem(itemStorage.updateItem(userId, item));
    }

    @Override
    public void removeItem(Long id) {
        itemStorage.getItemById(id).orElseThrow(() ->
                new NotFoundException("Такого товара нет."));
        itemStorage.removeItem(id);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemStorage.search(text)
                .stream()
                .map(ItemMapper::toItemDtoFromItem)
                .collect(Collectors.toList());
    }
}
