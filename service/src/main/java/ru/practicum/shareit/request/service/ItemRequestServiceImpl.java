package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto add(CreateUpdateItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользоваетль не найден."));

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build());

        return ItemRequestMapper.toItemRequestDtoFromItemRequest(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );

        return itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId(), PageRequest.of(from / size, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoFromItemRequest)
                .collect(Collectors.toList());
    }


    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользоваетль не найден."));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));

        return ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(requestId));
    }

    @Override
    public List<ItemRequestDto> getUserRequestsById(Long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользоваетль не найден."));

        return itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(from / size, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoFromItemRequest)
                .collect(Collectors.toList());
    }
}
