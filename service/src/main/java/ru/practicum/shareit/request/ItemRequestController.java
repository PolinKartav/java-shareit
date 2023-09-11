package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Constant.REQUEST_HEADER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                              @RequestBody CreateUpdateItemRequestDto itemRequestDto) {
        return itemRequestService.add(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getUserRequestsById(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
