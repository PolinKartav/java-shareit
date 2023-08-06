package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                              @Validated(OnCreate.class) @RequestBody CreateUpdateItemDto createUpdateItemDto) {
        return itemService.createItem(userId, createUpdateItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItems(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemService.getAllItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @Validated(OnUpdate.class) @RequestBody CreateUpdateItemDto createUpdateItemDto) {
        return itemService.updateItem(userId, itemId, createUpdateItemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                           @PathVariable @NotNull long itemId) {
        itemService.removeItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                @RequestParam String text) {
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CreateUpdateCommentDto createUpdateCommentDto) {
        return itemService.createComment(userId, itemId, createUpdateCommentDto);
    }
}
