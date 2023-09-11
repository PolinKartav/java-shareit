package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                              @RequestBody CreateUpdateItemDto createUpdateItemDto) {
        return itemService.createItem(userId, createUpdateItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                               @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {

        return itemService.getAllItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody CreateUpdateItemDto createUpdateItemDto) {
        return itemService.updateItem(userId, itemId, createUpdateItemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                           @PathVariable long itemId) {
        itemService.removeItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                @RequestParam(name = "text") String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        if ((text == null) || (text.isBlank())) {
            return List.of();
        }
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody CreateUpdateCommentDto createUpdateCommentDto) {
        return itemService.createComment(userId, itemId, createUpdateCommentDto);
    }
}
