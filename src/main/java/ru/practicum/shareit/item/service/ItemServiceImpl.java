package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(long userId, CreateUpdateItemDto createUpdateItemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользоваетль не найден."));

        Item item = ItemMapper.toItemFromCreateUpdateItemDto(createUpdateItemDto);
        item.setOwner(user);

        return ItemMapper.toItemDtoFromItem(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользоваетль не найден."));
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Такого товара нет."));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            return ItemMapper.toItemDtoWithBookingsFromItem(item);
        } else {
            return ItemMapper.toItemDtoFromItem(item);
        }
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDtoWithBookingsFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, CreateUpdateItemDto createUpdateItemDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет."));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такого товара нет."));
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Такого товара нет.");
        }

        if (createUpdateItemDto.getName() != null && !createUpdateItemDto.getName().isBlank()) {
            item.setName(createUpdateItemDto.getName());
        }

        if (createUpdateItemDto.getDescription() != null && !createUpdateItemDto.getDescription().isBlank()) {
            item.setDescription(createUpdateItemDto.getDescription());
        }

        if (createUpdateItemDto.getAvailable() != null) {
            item.setAvailable(createUpdateItemDto.getAvailable());
        }

        return ItemMapper.toItemDtoFromItem(itemRepository.save(item));
    }

    @Override
    public void removeItem(long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет."));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такого товара нет."));
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Такого товара нет.");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден."));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CreateUpdateCommentDto createUpdateCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь не найден."));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("вещь не найдена."));

        if (isBookingByUser(user, item)) {
            Comment comment = CommentMapper.toCommentFromCreateUpdateCommentDto(createUpdateCommentDto);

            comment.setAuthor(user);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            return CommentMapper.toCommentDtoFromComment(commentRepository.save(comment));
        } else {
            throw new ValidationException(String.format(
                    "Пользователь с ID = %s не брал в аренду вещь с ID = %s", userId, itemId));
        }
    }

    private @NotNull Boolean isBookingByUser(User user, @NotNull Item item) {
        LocalDateTime currentTime = LocalDateTime.now();
        return item.getBookings() != null && item.getBookings().stream()
                .anyMatch(t -> t.getBooker().equals(user)
                        && t.getEnd().isBefore(currentTime)
                        && t.getStatus().equals(Status.APPROVED));
    }
}

