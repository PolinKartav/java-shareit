package ru.practicum.shareit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.GetBookingForItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.*;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDtoFromItem(Item item) {
        SortedSet<CommentDto> comments = new TreeSet<>(orderByCreatedDesc);

        if (item.getComments() != null) {
            comments.addAll(item.getComments()
                    .stream()
                    .map(CommentMapper::toCommentDtoFromComment)
                    .collect(Collectors.toSet()));
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .build();
    }

    public ItemDto toItemDtoWithBookingsFromItem(Item item) {
        LocalDateTime currentTime = LocalDateTime.now();

        ItemDto itemDto = toItemDtoFromItem(item);

        Set<Booking> bookings = item.getBookings();

        if (bookings != null) {
            Booking lastBooking = bookings
                    .stream()
                    .sorted(orderByStartDateDesc)
                    .filter(t -> t.getStart().isBefore(currentTime) &&
                            t.getStatus().equals(Status.APPROVED))
                    .findFirst()
                    .orElse(null);

            Booking nextBooking = bookings
                    .stream()
                    .sorted(orderByStartDateAsc)
                    .filter(t -> t.getStart().isAfter(currentTime) &&
                            t.getStatus().equals(Status.APPROVED))
                    .findFirst()
                    .orElse(null);

            itemDto.setLastBooking(BookingMapper.toGetBookingForItemDtoFromBooking(lastBooking));
            itemDto.setNextBooking(BookingMapper.toGetBookingForItemDtoFromBooking(nextBooking));
        }

        return itemDto;
    }

    public GetBookingForItemDto toGetBookingForItemDtoFromItem(Item item) {
        return GetBookingForItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public Item toItemFromCreateUpdateItemDto(CreateUpdateItemDto createUpdateItemDto) {
        return Item.builder()
                .name(createUpdateItemDto.getName())
                .description(createUpdateItemDto.getDescription())
                .available(createUpdateItemDto.getAvailable())
                .build();
    }
}
