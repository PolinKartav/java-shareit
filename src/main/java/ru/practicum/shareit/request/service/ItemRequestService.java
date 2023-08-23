package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(CreateUpdateItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
    List<ItemRequestDto> getUserRequestsById(Long userId, int from, int size);
}
