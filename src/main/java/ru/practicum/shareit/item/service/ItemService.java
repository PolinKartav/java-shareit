package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, CreateUpdateItemDto createUpdateItemDto);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> getAllItems(Long userId, int from, int size);

    ItemDto updateItem(long userId, long itemId, CreateUpdateItemDto createUpdateItemDto);

    void removeItem(long id, long userId);

    List<ItemDto> search(String text, int from, int size);

    CommentDto createComment(long userId, long itemId, CreateUpdateCommentDto commentDto);
}
