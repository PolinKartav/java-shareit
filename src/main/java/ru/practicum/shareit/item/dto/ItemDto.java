package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.GetBookingForGetItemDto;

import java.util.SortedSet;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private GetBookingForGetItemDto lastBooking;
    private GetBookingForGetItemDto nextBooking;
    private SortedSet<CommentDto> comments;
}
