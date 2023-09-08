package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Comparator;

public class Constant {
    public static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    public static final Comparator<CommentDto> orderByCreatedDesc = (a, b) -> {
        if (a.getCreated().isAfter(b.getCreated())) {
            return 1;
        } else if (a.getCreated().isBefore(b.getCreated())) {
            return -1;
        } else {
            return 0;
        }
    };

    public static final Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };

    public static final Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };
}
