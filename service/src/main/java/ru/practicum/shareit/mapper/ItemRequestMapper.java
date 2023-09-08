package ru.practicum.shareit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDtoFromItemRequest(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ?
                        itemRequest.getItems()
                                .stream()
                                .map(ItemMapper::toItemDtoFromItem)
                                .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();

    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = toItemRequestDtoFromItemRequest(itemRequest);
        itemRequestDto.setItems(items != null ?
                items
                        .stream()
                        .map(ItemMapper::toItemDtoFromItem)
                        .collect(Collectors.toList()) :
                new ArrayList<>()
        );
        return itemRequestDto;
    }
}
